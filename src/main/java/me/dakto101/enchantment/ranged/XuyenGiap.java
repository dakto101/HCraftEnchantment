package me.dakto101.enchantment.ranged;

import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentType;

public class XuyenGiap extends CustomEnchantment {

	public XuyenGiap() {
		super("Xuyên giáp", "§7Nhận §6(2.5 X Cấp)%§7 xuyên giáp khi bắn trúng "
				+ "kẻ địch.", 3);
		setCanStack(false);
		setType(CustomEnchantmentType.RANGED);
	}
	
	@Override
	public String getDescription(int level, final LivingEntity user) {
		return this.getDescription().replace("(2.5 X Cấp)", "" + (2.5 * level));
	}

	@Override
    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (e.getFinalDamage() == 0) return;
		if (e.getDamage() == 0) return;
		if (e.getCause().equals(DamageCause.PROJECTILE)) {
			double damageReductionPercent = e.getFinalDamage()/e.getDamage();
			double pierce = 0.025 * level;
			double bonusDamage = (e.getDamage() - e.getFinalDamage()) * pierce * e.getDamage() / e.getFinalDamage();

			e.setDamage(e.getDamage() + bonusDamage);

			
			if (damageReductionPercent <= 0.4) target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 1);
			
		}
	}
	
}
