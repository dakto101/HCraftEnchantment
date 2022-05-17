package me.dakto101.enchantment.armor;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentType;

public class KhangPhep extends CustomEnchantment {

	public KhangPhep() {
		super("Kháng phép", "§7Giảm §90.25 X Cấp + 10% Giáp§7 sát thương phép "
				+ "nhận vào. ", 5);
		setCanStack(true);
		setType(CustomEnchantmentType.DEFENSE);
	}
	
	@Override
	public String getDescription(int level, final LivingEntity user) {
		double armor = user.getAttribute(Attribute.GENERIC_ARMOR).getValue();
		return this.getDescription().replace("0.25 X Cấp + 10% Giáp", "" + (0.25 * level + 0.1 * armor));
	}
	
	@Override
    public void applyDefense(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageEvent e) {
		
		double armor = user.getAttribute(Attribute.GENERIC_ARMOR).getValue();
		double reduction = 0.25 * level + armor * 0.1;
		if (e.getCause().equals(DamageCause.MAGIC)) {
			e.setDamage(e.getDamage() - reduction);
		}
	}

	
}
