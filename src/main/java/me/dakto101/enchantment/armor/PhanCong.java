package me.dakto101.enchantment.armor;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.scheduler.BukkitScheduler;

import me.dakto101.HCraftEnchantment;
import me.dakto101.api.Cooldown;
import me.dakto101.api.Cooldown.CooldownType;
import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentType;
import me.dakto101.util.HCraftDamageSource;

public class PhanCong extends CustomEnchantment {

	public PhanCong() {
		super("Phản công", "§7Phản lại §9(Cấp X 0.5 + 10% Giáp)§7 thành sát thương "
				+ "phép thuật lên kẻ địch sau khi bị đánh.", 5);
		setCanStack(true);
		setType(CustomEnchantmentType.DEFENSE);
		setCooldown(20);
	}
	
	@Override
	public String getDescription(int level, final LivingEntity user) {
		double armor = user.getAttribute(Attribute.GENERIC_ARMOR).getValue();
		return this.getDescription().replace("Cấp X 0.5 + 10% Giáp", "" + (level * 0.5 + 0.1 * armor));
	}
	
	@Override
    public void applyDefense(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageEvent e) {
		if (target == null) return;
		
		if (Cooldown.onCooldown(user.getUniqueId(), CooldownType.ARMOR_ENCHANTMENT)) return;
		
		if (e.getCause().equals(DamageCause.ENTITY_ATTACK) || 
			e.getCause().equals(DamageCause.ENTITY_SWEEP_ATTACK) || 
			e.getCause().equals(DamageCause.PROJECTILE)) {
			
			double armor = user.getAttribute(Attribute.GENERIC_ARMOR).getValue();
			double damage = level * 0.5 + armor * 0.1;

			// Tạo scheduled task để tránh deadlock
			BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
			s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
				try {
					HCraftDamageSource.damageThorns(user, target, (float) damage);
					target.getWorld().playSound(target.getLocation(), Sound.ENCHANT_THORNS_HIT, 1, 1);
					user.sendMessage("§6" + this.getName() + "§7 gây §a" + damage + ""
							+ "§7 sát thương phép cho mục tiêu.");
				} catch (Exception exception) {
					exception.printStackTrace();
					Bukkit.getServer().getLogger().info("Plugin HCraftEnchantment, "
							+ "enchant PhanCong bi loi. #1");
				}
			}, 1L);
			
			Cooldown.setCooldown(user.getUniqueId(), getCooldown(), CooldownType.ARMOR_ENCHANTMENT);
		}
	}

	
}
