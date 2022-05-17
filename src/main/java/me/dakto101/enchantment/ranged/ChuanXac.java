package me.dakto101.enchantment.ranged;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentType;

public class ChuanXac extends CustomEnchantment {

	public ChuanXac() {
		super("Chuẩn xác", "§7Bắn trúng kẻ địch ở khoảng cách càng xa sẽ tăng "
				+ "càng nhiều sát thương, tối đa ở 50 ô. Sát thương cộng thêm tối đa: "
				+ "§6116% X 1.2^(Cấp)§7.", 10);
		setCanStack(false);
		setType(CustomEnchantmentType.RANGED);
	}
	
	@Override
	public String getDescription(int level, final LivingEntity user) {
		return this.getDescription().replace("116% X 1.2^(Cấp)", "" + (116 * Math.pow(1.2, level)) + "%");
	}

	@Override
    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (e.getCause().equals(DamageCause.PROJECTILE)) {
			double distance = user.getLocation().distance(target.getLocation());
			if (distance > 50) distance = 50;
			double bonusDamage = (Math.pow(1.1, distance) - 1) * Math.pow(1.2, level);
			e.setDamage(e.getDamage() + bonusDamage);
		}
	}
	
}
