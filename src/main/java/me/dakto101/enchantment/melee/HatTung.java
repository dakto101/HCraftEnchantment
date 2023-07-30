package me.dakto101.enchantment.melee;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import me.dakto101.HCraftEnchantment;
import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentType;

public class HatTung extends CustomEnchantment {

	public HatTung() {
		super("Hất tung", "§7Đòn đánh cận chiến có §f(2 + 2 X Cấp)%§7 xác suất hất tung mục tiêu.", 10);
		setCanStack(false);
		setType(CustomEnchantmentType.MELEE);
	}
	
	@Override
	public String getDescription(int level, final LivingEntity user) {
		return this.getDescription().replace("(2 + 2 X Cấp)", "" + (2 + 2 * level));
	}

	@Override
    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		double chance = (2 + level * 2) * 0.01;
		Vector knockUp = new Vector(0, 0.9 + 0.025 * level, 0);
		if (Math.random() > chance) return; 
		if (e.getCause().equals(DamageCause.ENTITY_ATTACK) || 
				e.getCause().equals(DamageCause.ENTITY_SWEEP_ATTACK)) {

			BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
			s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
				target.setVelocity(target.getVelocity().add(knockUp));
				target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 2);
				for (int i = 0; i <= 40; i++) {
					target.getWorld().spawnParticle(Particle.CLOUD, target.getLocation().add(Math.random() * 2 - 1, Math.random() * 2 - 1, Math.random() * 2 - 1), 0,
							knockUp.getX(), knockUp.getY(), knockUp.getZ());
				}
			}, 1L);

		}
	}
	
}
