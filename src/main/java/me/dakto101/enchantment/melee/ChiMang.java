package me.dakto101.enchantment.melee;

import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;

import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentType;

@SuppressWarnings("deprecation")
public class ChiMang extends CustomEnchantment {

	public ChiMang() {
		super("Chí mạng", "§7Có xác suất §f(2.5 + Cấp X 2.5)%§7 gấp đôi "
				+ "sát thương cho đòn đánh cận chiến.", 3);
		setCanStack(false);
		setType(CustomEnchantmentType.MELEE);
	}
	
	@Override
	public String getDescription(int level, final LivingEntity user) {
		return this.getDescription().replace("(2.5 + Cấp X 2.5)", "" + (2.5 + level * 2.5));
	}

	@Override
    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		double chance = (2.5 + level * 2.5)/100;
		if (Math.random() > chance) return; 
		if (e.getCause().equals(DamageCause.ENTITY_ATTACK) || 
				e.getCause().equals(DamageCause.ENTITY_SWEEP_ATTACK)) {
			
			double rawDamage = e.getOriginalDamage(DamageModifier.BASE);
			if (rawDamage <= 0) return;
			e.setDamage(e.getDamage() + rawDamage);
			user.sendMessage("§e" + this.getName() + "§a gây thêm §e" + rawDamage 
					+ " §asát thương vật lý.");
			user.getWorld().playSound(user.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1, 1);
		}
		

	}
	
}
