package me.dakto101.enchantment.melee;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;

import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentType;

@SuppressWarnings("deprecation")
public class ChiTu extends CustomEnchantment {

	public ChiTu() {
		super("Chí tử", "§7Đòn đánh thường có xác suất thấp §f(0.5 X Cấp)%§7"
				+ " gây thêm sát thương cực mạnh bằng §6400%§7 sát thương vật lý.", 20);
		setCanStack(false);
		setType(CustomEnchantmentType.MELEE);
	}

	@Override
	public String getDescription(int level, final LivingEntity user) {
		return this.getDescription().replace("(0.5 X Cấp)", "" + (0.5 * level));
	}
	
	@Override
    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		double chance = (0.5 * level)/100;
		if (Math.random() > chance) return; 
		if (e.getCause().equals(DamageCause.ENTITY_ATTACK) || 
				e.getCause().equals(DamageCause.ENTITY_SWEEP_ATTACK)) {
			
			double bonusDamage = e.getOriginalDamage(DamageModifier.BASE) * 4;
			if (bonusDamage <= 0) return;
			e.setDamage(e.getDamage() + bonusDamage);
			user.sendMessage("§e" + this.getName() + "§a gây thêm §e" + bonusDamage 
					+ " §asát thương vật lý.");
			user.getWorld().playSound(user.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1, 1);
			target.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, target.getLocation(), 100);
		}
		

	}
	
}
