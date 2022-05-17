package me.dakto101.enchantment.melee;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;

import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentType;

@SuppressWarnings("deprecation")
public class SacNet extends CustomEnchantment {

	public SacNet() {
		super("Sắc nét", "§7Đòn đánh thường gây thêm §6(2 + Cấp X 2)%§7 sát thương vật lý.", 1);
		setCanStack(false);
		setType(CustomEnchantmentType.MELEE);
	}
	
	@Override
	public String getDescription(int level, final LivingEntity user) {
		return this.getDescription().replace("(2 + Cấp X 2)", "" + (2 + level * 2));
	}

	@Override
    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (e.getCause().equals(DamageCause.ENTITY_ATTACK) || 
				e.getCause().equals(DamageCause.ENTITY_SWEEP_ATTACK)) {
			
			double bonusDamage = e.getOriginalDamage(DamageModifier.BASE) * (0.02 + 0.02 * level);
			e.setDamage(e.getDamage() + bonusDamage);
		}
		

	}
	
}
