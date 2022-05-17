package me.dakto101.enchantment.ranged;

import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentType;

public class HuyetCung extends CustomEnchantment {

	public HuyetCung() {
		super("Huyết cung", "§7Mỗi phát bắn trúng sẽ hồi máu tương đương với "
				+ "§c(3 X Cấp)%§7 sát thương gây ra.", 1);
		setCanStack(false);
		setType(CustomEnchantmentType.RANGED);
	}
	
	@Override
	public String getDescription(int level, final LivingEntity user) {
		return this.getDescription().replace("(3 X Cấp)", "" + (3 * level));
	}

	@Override
    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (e.getCause().equals(DamageCause.PROJECTILE)) {
			double amount = e.getFinalDamage() * 0.03 * level;
			double maxHealth = user.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
			double health = user.getHealth();
			user.setHealth((health + amount >= maxHealth) ? maxHealth : health + amount);
			if (e.getFinalDamage() >= level * 3 + 7) user.getWorld().playSound(user.getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 1);
		}
	}
	
}
