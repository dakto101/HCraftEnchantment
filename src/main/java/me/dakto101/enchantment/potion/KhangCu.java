package me.dakto101.enchantment.potion;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentType;

public class KhangCu extends CustomEnchantment {

	public KhangCu() {
		super("Kháng cự", "§7Đòn đánh cận chiến hoặc khi bị đánh có §f5%§7 xác suất "
				+ "nhận hiệu ứng §fKháng cự I§7 trong §f(1 + Cấp)§7 giây. "
				+ "Xác suất tăng thành §f10%§7 đối với đòn đánh xa.", 5);
		setCanStack(true);
		setType(CustomEnchantmentType.POTION);
	}
	
	@Override
	public String getDescription(int level, final LivingEntity user) {
		return this.getDescription().replace("(1 + Cấp)", "" + (1 + level));
	}

	@Override
    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		double chance = 0d;
		if (e.getCause().equals(DamageCause.PROJECTILE)) {
			chance = (10d)/100;
		}
		if ((e.getCause().equals(DamageCause.ENTITY_ATTACK)) || 
			e.getCause().equals(DamageCause.ENTITY_SWEEP_ATTACK)) {
			chance = (5d)/100;
		}
		if (chance <= 0d) return;
		if (Math.random() > chance) return; 
		
		int duration = 20 * (1 + level);
		user.addPotionEffect(
				new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 
				duration, 
				0, 
				true, 
				true, 
				true));
	}
	
	@Override
    public void applyDefense(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageEvent e) {
		if (target != null) {
			double chance = 0d;
			if (e.getCause().equals(DamageCause.PROJECTILE) || 
				e.getCause().equals(DamageCause.ENTITY_ATTACK) || 
				e.getCause().equals(DamageCause.ENTITY_SWEEP_ATTACK)) {
				chance = 0.05;
			}

			if (chance <= 0d) return;
			if (Math.random() > chance) return; 
			
			int duration = 20 * (1 + level);
			user.addPotionEffect(
					new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 
					duration, 
					0, 
					true, 
					true, 
					true));
		}
	}

	
}
