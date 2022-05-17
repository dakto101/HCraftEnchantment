package me.dakto101.enchantment.melee;

import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;

import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentType;

@SuppressWarnings("deprecation")
public class CanBang extends CustomEnchantment {

	public CanBang() {
		super("Cân bằng", "§7Nếu kẻ địch có lượng máu hiện tại lớn hơn, đòn đánh thường "
				+ "gây thêm §6(5 + 5 X Cấp)%§7 sát thương vật lý.", 4);
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
			
			if (target.getHealth() > user.getHealth()) {
				double bonusDamage = e.getOriginalDamage(DamageModifier.BASE) * (0.05 + 0.05 * level);
				e.setDamage(e.getDamage() + bonusDamage);
				target.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, target.getLocation(), level * 10 <= 100 ? level * 10 : 100);
			}

		}
		

	}
	
}
