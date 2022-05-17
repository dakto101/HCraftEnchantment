package me.dakto101.enchantment.ranged;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityShootBowEvent;

import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentType;

public class DanHoi extends CustomEnchantment {

	public DanHoi() {
		super("Đàn hồi", "§7Tăng độ đàn hồi của cung giúp tăng lực bắn của cung. "
				+ "Lực bắn tăng §f5%§7 với mỗi cấp phù phép.", 1);
		setCanStack(false);
		setType(CustomEnchantmentType.RANGED);
	}
	
	@Override
	public String getDescription(int level, final LivingEntity user) {
		return this.getDescription().replace("5%§7 với mỗi cấp phù phép.", "" + (5 * level) + "%§7.");
	}
	
	@Override
    public void applyProjectile(final LivingEntity user, final int level, final EntityShootBowEvent e) {
		e.getProjectile().setVelocity(e.getProjectile().getVelocity().multiply(1 + level * 0.05));
	}
	
}
