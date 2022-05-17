package me.dakto101.enchantment.armor;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.scheduler.BukkitScheduler;

import me.dakto101.HCraftEnchantment;
import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentType;

public class ThieuDot extends CustomEnchantment {

	public ThieuDot() {
		super("Thiêu đốt", "§7Khi bị đánh có §f(2.5 X Cấp)% xác suất thiêu cháy kẻ "
				+ "địch trong §f4§7 giây. ", 11d);
		setCanStack(true);
		setType(CustomEnchantmentType.DEFENSE);
	}
	
	@Override
	public String getDescription(int level, final LivingEntity user) {
		return this.getDescription().replace("(2.5 X Cấp)", "" + (2.5 * level));
	}
	
	@Override
    public void applyDefense(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageEvent e) {
		if (target == null) return;
		
		if (e.getCause().equals(DamageCause.ENTITY_ATTACK) || 
			e.getCause().equals(DamageCause.ENTITY_SWEEP_ATTACK) || 
			e.getCause().equals(DamageCause.PROJECTILE)) {
			
			double chance = (2.5 * level) * 0.01;
			int duration = 80;
			
			if (Math.random() > chance) return;
			
			BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
			s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
				target.setFireTicks(target.getFireTicks() + duration);
			}, 1L);

		}
	}

	
}
