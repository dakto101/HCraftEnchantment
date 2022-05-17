package me.dakto101.enchantment.melee;

import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentType;

public class TuChien extends CustomEnchantment {

	public TuChien() {
		super("Tử chiến", "§7Nếu bạn còn dưới §f30%§7 máu, đòn đánh thường tăng thêm "
				+ "§60.5 X Cấp§7 sát thương vật lý.", 4);
		setCanStack(false);
		setType(CustomEnchantmentType.MELEE);
	}
	
	@Override
	public String getDescription(int level, final LivingEntity user) {
		return this.getDescription().replace("0.5 X Cấp", "" + (0.5 * level));
	}

	@Override
    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (e.getCause().equals(DamageCause.ENTITY_ATTACK) || 
				e.getCause().equals(DamageCause.ENTITY_SWEEP_ATTACK)) {
			
			double maxHealth = user.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
			if (user.getHealth() / maxHealth < 0.3) {
				double bonusDamage = 0.5 * level;
				e.setDamage(e.getDamage() + bonusDamage);
				target.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, target.getLocation(), level * 3 <= 100 ? level * 3 : 20);
			}

		}
		

	}
	
}
