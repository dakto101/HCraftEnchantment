package me.dakto101.enchantment.armor;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentType;

public class BenBi extends CustomEnchantment {

	public BenBi() {
		super("Bền bỉ", "§7Chặn §6(0.2 + 0.1 X Cấp)§7 với các đòn đánh cận chiến và chặn "
				+ "§6(0.3 + 0.15 X Cấp)§7 cho đòn đánh xa nhận vào.", 1);
		setCanStack(true);
		setType(CustomEnchantmentType.DEFENSE);
	}
	
	@Override
	public String getDescription(int level, final LivingEntity user) {
		return this.getDescription().replace("0.2 + 0.1 X Cấp", "" + (0.2 + 0.1 * level)).replace("0.3 + 0.15 X Cấp", "" + (0.3 + 0.15 * level));
	}
	
	@Override
    public void applyDefense(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageEvent e) {
		
		double reduction = 0.2 + 0.1 * level;
		
		if (e.getCause().equals(DamageCause.ENTITY_ATTACK) || 
			e.getCause().equals(DamageCause.ENTITY_SWEEP_ATTACK)) {
			e.setDamage(e.getDamage() - reduction);
		}
		
		if (e.getCause().equals(DamageCause.PROJECTILE)) {
			reduction *= 1.5;
			e.setDamage(e.getDamage() - reduction);
		}
		
	}

	
}
