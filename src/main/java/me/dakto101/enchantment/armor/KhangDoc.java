package me.dakto101.enchantment.armor;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentType;

public class KhangDoc extends CustomEnchantment {

	public KhangDoc() {
		super("Kháng độc", "§7Giảm §a(5 X Cấp)%§7 cho sát thương độc tính nhận vào.", 5);
		setCanStack(true);
		setType(CustomEnchantmentType.DEFENSE);
	}
	
	@Override
	public String getDescription(int level, final LivingEntity user) {
		return this.getDescription().replace("(5 X Cấp)", "" + (5 * level) + "%");
	}
	
	@Override
    public void applyDefense(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageEvent e) {
	
		double reduction = 0.05 * level * e.getDamage();
		
		if (e.getCause().equals(DamageCause.POISON)) {
			e.setDamage(e.getDamage() - reduction);
		}
		
	}

	
}
