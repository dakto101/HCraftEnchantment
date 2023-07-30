package me.dakto101.skill.archery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitScheduler;

import me.dakto101.HCraftEnchantment;
import me.dakto101.api.Cooldown;
import me.dakto101.api.Cooldown.CooldownType;
import me.dakto101.api.Skill;
import me.dakto101.api.SkillEnum;
import me.dakto101.api.SkillType;
import me.dakto101.api.Toggle;
import me.dakto101.api.Toggle.ToggleType;

public class HuyetCung extends Skill {
	
	private static final String HUYET_CUNG = "§c§lHuyết cung";

	public HuyetCung() {
		super(SkillEnum.HUYET_CUNG, Arrays.asList(
				"§7§nKích hoạt:§r§7 Bắn ra mũi tên hút máu, gây thêm §6(2 + 0.25 X Cấp)§7 sát thương ",
				"§7vật lý và hồi §c(1 + 0.15 X Cấp)§7 máu nếu bắn trúng. (Shift + Click trái)",
				"",
				"§7§nBị động:",
				"§7- Nhận §c3% + 1% X Cấp§7 hút máu gây ra bởi mũi tên, đạn đạo của bạn."
				), 10d, SkillType.ARCHERY);
		setFoodRequire(4);
		setActiveCooldown(6);
		setIcon(Material.APPLE);
		
	}
	
	@Override
    public List<String> getDescription(int level, final LivingEntity user) {
		List<String> description = new ArrayList<String>(this.getDescription());
    	description.replaceAll(s -> s.replace("(2 + 0.25 X Cấp)", "" + (2 + 0.25 * level)));
    	description.replaceAll(s -> s.replace("(1 + 0.15 X Cấp)", "" + (1 + 0.15 * level)));
    	description.replaceAll(s -> s.replace("3% + 1% X Cấp", "" + (3 + level * 1) + "%"));
    	return description;
    }
	
	//Active
	@Override
	public void applyInteractBlock(final Player user, final int level, final PlayerInteractEvent e) { 
		if (e.getAction().equals(Action.LEFT_CLICK_AIR) || 
				e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			if (!user.isSneaking()) return;
			boolean toggle = Toggle.getToggle(user.getUniqueId(), ToggleType.ACTIVE_SKILL);
			Toggle.setToggle(user.getUniqueId(), !toggle, ToggleType.ACTIVE_SKILL);
			Toggle.sendMessage(user, ToggleType.ACTIVE_SKILL);
			user.playSound(user.getLocation(), Sound.UI_BUTTON_CLICK, 1, toggle ? 0.5f : 0.7f);
		}
	}
	
	//Active
	@Override
	public void applyProjectile(final LivingEntity u, final int level, final EntityShootBowEvent e) {
		//Condition
		Player user = (Player) u;
		if (!(user instanceof Player)) return;
		if (!Toggle.getToggle(user.getUniqueId(), ToggleType.ACTIVE_SKILL)) return;
		if (Cooldown.onCooldown(user.getUniqueId(), CooldownType.ACTIVE_SKILL)) {
			return;
		}
		if (user.getFoodLevel() < getFoodRequire()) {
			user.sendMessage("§cKhông đủ điểm thức ăn!"); 
			return;
		}
		//Param
		Entity proj = e.getProjectile();
		World w = proj.getWorld();
		//Code
		
		proj.setCustomName(HUYET_CUNG);
		proj.setCustomNameVisible(true);
		proj.setGlowing(true);
		proj.getWorld().playSound(proj.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
		
		BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
		int taskID = s.scheduleSyncRepeatingTask(HCraftEnchantment.plugin, () -> {
			if (!proj.isDead()) w.spawnParticle(Particle.REDSTONE, proj.getLocation(), 0, 0, 0, 0, new DustOptions(Color.RED, 1));
		}, 0L, 1L);
		s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
			proj.remove();
			s.cancelTask(taskID);
		}, 200L);
		//Cooldown and food
		Cooldown.setCooldown(user.getUniqueId(), getActiveCooldown(), CooldownType.ACTIVE_SKILL);
		user.setFoodLevel(user.getFoodLevel() - getFoodRequire());	
	}
	
	//Passive
	@Override
    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (e.getCause().equals(DamageCause.PROJECTILE)) {
			String name = e.getDamager().getCustomName();
			if (name != null && name.equals(HUYET_CUNG)) {
				//Param
				double bonusDmg = 2 + 0.25 * level;
				double bonusHealth = 1 + 0.15 * level;
				double maxHealth = user.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
				double health = user.getHealth();
				
				//Code
				e.setDamage(e.getDamage() + bonusDmg);
				user.setHealth((health + bonusHealth >= maxHealth) ? maxHealth : health + bonusHealth);
				
				user.getWorld().playSound(user.getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 1);
				user.getWorld().spawnParticle(Particle.HEART, user.getLocation(), 5 + level);
			}
			
			//Passive
			//Param
			double leech = 0.03 + 0.01 * level;
			//Code
			double amount = e.getFinalDamage() * leech;
			double maxHealth = user.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
			double health = user.getHealth();
			user.setHealth((health + amount >= maxHealth) ? maxHealth : health + amount);
		}
	}
}
