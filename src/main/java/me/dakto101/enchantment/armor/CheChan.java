package me.dakto101.enchantment.armor;

import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentType;

public class CheChan extends CustomEnchantment {

	public CheChan() {
		super("Che chắn", "§7Có xác suất §f0.5% X Cấp§7 chặn được đòn đánh thường của "
				+ "kẻ địch.", 2);
		setCanStack(true);
		setType(CustomEnchantmentType.DEFENSE);
	}
	
	@Override
	public String getDescription(int level, final LivingEntity user) {
		return this.getDescription().replace("0.5% X Cấp", "" + (0.5 * level) + "%");
	}
	
	@Override
    public void applyDefense(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageEvent e) {
		
		if (e.getCause().equals(DamageCause.ENTITY_ATTACK) || 
			e.getCause().equals(DamageCause.ENTITY_SWEEP_ATTACK) || 
			e.getCause().equals(DamageCause.PROJECTILE)) {
			
			double chance = (0.5 * level) * 0.01;
			
			if (Math.random() > chance) return;
			
			user.sendMessage("§6" + this.getName() + "§7 đã chắn một đòn đánh thường cho bạn.");
			user.getWorld().playSound(user.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1, 1);
			e.setDamage(0);

		}
		
	}

	
}
