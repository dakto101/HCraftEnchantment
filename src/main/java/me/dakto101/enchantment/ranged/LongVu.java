package me.dakto101.enchantment.ranged;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentType;

public class LongVu extends CustomEnchantment {

	public LongVu() {
		super("Lông vũ", "§7Nếu kéo tối đa lực, mũi tên sẽ trở nên không trọng lực, "
				+ "cải thiện độ chính xác. Yêu cầu §b1§7 lông vũ.", 200);
		setCanStack(false);
		setType(CustomEnchantmentType.RANGED);
	}
	
	
	
	@Override
    public void applyProjectile(final LivingEntity user, final int level, final EntityShootBowEvent e) {
		Entity projectile = e.getProjectile();
		if (!(projectile instanceof Arrow)) return;
		if (e.getForce() < 0.9) return;
		if (user instanceof Player) {
			if (((Player) user).getInventory().contains(Material.FEATHER, 1)) {
				((Player) user).getInventory().removeItem(new ItemStack(Material.FEATHER, 1));
			} else return;
		}
		projectile.setGravity(false);
		projectile.setVelocity(e.getProjectile().getVelocity().multiply(0.75));
	}
	
}
