package me.dakto101.skill.archery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
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

public class PhaGiap extends Skill {
	
	private static String PHA_GIAP = "§3§lPhá giáp";

	public PhaGiap() {
		super(SkillEnum.PHA_GIAP, Arrays.asList(
				"§7§nKích hoạt:§r§7 Kéo tối đa lực bắn sẽ xuất hiện mũi tên phá giáp gây thêm §60.1 + 0.02 X Cấp§7 ",
				"§7sát thương cho mỗi điểm phòng thủ của mục tiêu. Tối đa: §62 + 0.4 X Cấp§7 sát thương. ",
				"§7(Shift + Click trái)",
				"",
				"§7§nBị động:",
				"§7- Nhận §f10% + Cấp X 1%§7 xuyên giáp gây ra bởi mũi tên, đạn đạo của bạn."
				), 10d, SkillType.ARCHERY);
		setFoodRequire(2);
		setCooldown(0);
		setIcon(Material.ARROW);
		
	}
	
	@Override
    public List<String> getDescription(int level, final LivingEntity user) {
		List<String> description = new ArrayList<String>(this.getDescription());
    	description.replaceAll(s -> s.replace("0.1 + 0.02 X Cấp", "" + (0.1 + 0.02 * level)));
    	description.replaceAll(s -> s.replace("2 + 0.4 X Cấp", "" + (2 + 0.4 * level)));
    	description.replaceAll(s -> s.replace("10% + Cấp X 1%", "" + (10 + level * 1) + "%"));
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
		if (e.getForce() < 0.9) return;
		//Param
		//Code
		Entity proj = e.getProjectile();
		proj.setCustomName(PHA_GIAP);
		proj.setCustomNameVisible(true);
		proj.setGlowing(true);
		
		BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
		
		s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
			proj.remove();
		}, 200L);
		//Cooldown and food
		Cooldown.setCooldown(user.getUniqueId(), getCooldown(), CooldownType.ACTIVE);
		user.setFoodLevel(user.getFoodLevel() - getFoodRequire());	
	}
	
	//Active
	@Override
    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (!(target instanceof LivingEntity)) return;
		if (e.getFinalDamage() == 0) return;
		if (e.getDamage() == 0) return;
		//Active
		if (e.getCause().equals(DamageCause.PROJECTILE)) {
			String name = e.getDamager().getCustomName();
			if (name != null && name.equals(PHA_GIAP)) {
				//Param
				double dmgPerDefPoint = 0.1 + 0.02 * level;
				double targetDefPoint = target.getAttribute(Attribute.GENERIC_ARMOR).getValue();
				//Code
				e.setDamage(e.getDamage() + dmgPerDefPoint * targetDefPoint);
				target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 2);
			}
		//Passive
			//Param
			double damageReductionPercent = e.getFinalDamage()/e.getDamage();
			double pierce = 0.1 + 0.01 * level; 
			double bonusDamage = (e.getDamage() - e.getFinalDamage()) * pierce * e.getDamage() / e.getFinalDamage();
			//Code
			e.setDamage(e.getDamage() + bonusDamage);
			if (damageReductionPercent <= 0.25) target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 1);
		}
		
	}
}
