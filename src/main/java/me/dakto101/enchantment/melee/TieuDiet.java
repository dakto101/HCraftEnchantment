package me.dakto101.enchantment.melee;

import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;

import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentType;

@SuppressWarnings("deprecation")
public class TieuDiet extends CustomEnchantment {

	public TieuDiet() {
		super("Tiêu diệt", "§7Nếu kẻ địch có lượng máu thấp hơn §f30%§7, đòn đánh gây thêm"
				+ " §6(5 + 5 X Cấp)%§7 sát thương vật lý.", 4);
		setCanStack(false);
		setType(CustomEnchantmentType.MELEE);
	}

	@Override
	public String getDescription(int level, final LivingEntity user) {
		return this.getDescription().replace("(5 + 5 X Cấp)", "" + (5 + 5 * level));
	}
	
	@Override
    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (e.getCause().equals(DamageCause.ENTITY_ATTACK) || 
				e.getCause().equals(DamageCause.ENTITY_SWEEP_ATTACK)) {
			
			double maxHealth = target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
			if (target.getHealth() / maxHealth < 0.3) {
				double bonusDamage = e.getOriginalDamage(DamageModifier.BASE) * (0.05 + 0.05 * level);
				e.setDamage(e.getDamage() + bonusDamage);
				target.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, target.getLocation(), level * 10 <= 100 ? level * 10 : 100);
			}

		}
		

	}
	
}
