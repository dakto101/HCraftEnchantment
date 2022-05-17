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
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import me.dakto101.HCraftEnchantment;
import me.dakto101.api.Cooldown;
import me.dakto101.api.Cooldown.CooldownType;
import me.dakto101.api.Skill;
import me.dakto101.api.SkillEnum;
import me.dakto101.api.SkillType;
import me.dakto101.api.Toggle;
import me.dakto101.api.Toggle.ToggleType;
import me.dakto101.util.DamageSourceEnum;
import me.dakto101.util.HCraftDamageSource;
import me.dakto101.util.ParticleEffect;

public class KichDoc extends Skill {
	
	private static final String KICH_DOC = "§2§lKịch độc";

	public KichDoc() {
		super(SkillEnum.KICH_DOC, Arrays.asList(
				"§7§nKích hoạt:§r§7 Bắn ra mũi tên độc, gây thêm §9(4 + 0.35 X Cấp)§7 sát thương phép ",
				"§7trong 4 giây, làm mục tiêu không thể di chuyển được. (Shift + Click trái)",
				"",
				"§7§nBị động:",
				"§7- Phát bắn trúng có §f10%§7 gây độc cho mục tiêu trong §f(2.5 + 0.5 X Cấp)§7 giây."
				), 10d, SkillType.ARCHERY);
		setFoodRequire(3);
		setCooldown(15);
		setIcon(Material.SPIDER_EYE);
		
	}
	
	@Override
    public List<String> getDescription(int level, final LivingEntity user) {
		List<String> description = new ArrayList<String>(this.getDescription());
    	description.replaceAll(s -> s.replace("(4 + 0.35 X Cấp)", "" + (4 + 0.35 * level)));
    	description.replaceAll(s -> s.replace("(2.5 + 0.5 X Cấp)", "" + (2.5 + 0.5 * level)));
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
		if (Cooldown.onCooldown(user.getUniqueId(), CooldownType.ACTIVE)) {
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
		
		if (!(proj instanceof AbstractArrow)) return;
		proj.setCustomName(KICH_DOC);
		proj.setCustomNameVisible(true);
		proj.setGlowing(true);
		proj.getWorld().playSound(proj.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
		
		BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
		int taskID = s.scheduleSyncRepeatingTask(HCraftEnchantment.plugin, () -> {
			if (!proj.isDead()) w.spawnParticle(Particle.REDSTONE, proj.getLocation(), 0, 0, 0, 0, new DustOptions(Color.GREEN, 1));
		}, 0L, 1L);
		s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
			proj.remove();
			s.cancelTask(taskID);
		}, 200L);
		//Cooldown and food
		Cooldown.setCooldown(user.getUniqueId(), getCooldown(), CooldownType.ACTIVE);
		user.setFoodLevel(user.getFoodLevel() - getFoodRequire());	
	}
	
	@Override
    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (e.getCause().equals(DamageCause.PROJECTILE)) {
			//Active
			String name = e.getDamager().getCustomName();
			if (name != null && name.equals(KICH_DOC)) {
				//Param
				float damage = (float) (4 + level * 0.35);
				long duration = 80;
				long interval = 3;
				//Code
				BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
				int taskID = s.scheduleSyncRepeatingTask(HCraftEnchantment.plugin, () -> {
					HCraftDamageSource.damage(user, target, DamageSourceEnum.MAGIC, damage/(duration/interval));
					target.setVelocity(new Vector(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5));
				}, 0L, interval);
				s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
					
					s.cancelTask(taskID);
				}, duration);
			}
			//Passive
			//Param
			int duration = (int) ((2.5 + 0.5 * level)*20);
			double chance = 0.1;
			//Code
			if (Math.random() < chance) {
				target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration, 1, true, true, true));
				target.getWorld().playSound(target.getLocation(), Sound.ENTITY_CHICKEN_EGG, 2, 1);
				ParticleEffect.createNearbyParticle(target.getEyeLocation(), 50, Particle.REDSTONE, 1, 1, 1, new Vector(0, 0, 0), new DustOptions(Color.GREEN, 1));
			}
			
		}
	}
}
