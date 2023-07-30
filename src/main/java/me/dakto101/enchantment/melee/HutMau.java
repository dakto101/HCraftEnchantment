package me.dakto101.enchantment.melee;

import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentType;

public class HutMau extends CustomEnchantment {

	public HutMau() {
		super("Hút máu", "§7Đòn đánh cận chiến hồi máu bằng với §c(2.5 X Cấp)%§7 "
				+ "sát thương gây ra.", 1.5);
		setCanStack(false);
		setType(CustomEnchantmentType.MELEE);
	}
	
	@Override
	public String getDescription(int level, final LivingEntity user) {
		return this.getDescription().replace("(2.5 X Cấp)", "" + (2.5 * level));
	}

	@Override
    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (e.getCause().equals(DamageCause.ENTITY_ATTACK) || 
				e.getCause().equals(DamageCause.ENTITY_SWEEP_ATTACK)) {
			
			double amount = e.getFinalDamage() * 0.025 * level;
			double maxHealth = user.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
			double health = user.getHealth();
			user.setHealth(Math.min(health + amount, maxHealth));
			if (e.getFinalDamage() >= level * 3 + 7) user.getWorld().playSound(user.getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 1);
		}
		

	}
	
}
