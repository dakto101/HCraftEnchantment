package me.dakto101.enchantment.melee;

import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentType;

public class XuyenPha extends CustomEnchantment {

	public XuyenPha() {
		super("Xuyên phá", "§7Nhận §6(2.5 X Cấp)%§7 xuyên giáp khi tấn công kẻ "
				+ "địch bằng đòn đánh cận chiến.", 4);
		setCanStack(false);
		setType(CustomEnchantmentType.MELEE);
	}
	
	@Override
	public String getDescription(int level, final LivingEntity user) {
		return this.getDescription().replace("(2.5 X Cấp)", "" + (2.5 * level));
	}

	@Override
    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (e.getFinalDamage() < 0.1) return;
		if (e.getDamage() < 0.1) return;
		if (e.getCause().equals(DamageCause.ENTITY_ATTACK) || 
				e.getCause().equals(DamageCause.ENTITY_SWEEP_ATTACK)) {
			
			double damageReductionPercent = e.getFinalDamage()/e.getDamage();
			double pierce = 0.025 * level;
			double bonusDamage = (e.getDamage() - e.getFinalDamage()) * pierce * e.getDamage() / e.getFinalDamage();

			if (Math.abs(bonusDamage) > 100) return;
			e.setDamage(e.getDamage() + bonusDamage);
			if (damageReductionPercent <= 0.4) target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 1);
		
		}
	}
	
}
