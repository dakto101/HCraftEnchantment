package me.dakto101.enchantment.melee;

import org.bukkit.Particle;
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
		if (Math.random() > chance) return; 
		if (e.getCause().equals(DamageCause.ENTITY_ATTACK) || 
				e.getCause().equals(DamageCause.ENTITY_SWEEP_ATTACK)) {

			BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
			s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
				target.setVelocity(new Vector(0, 1 + level * 0.1, 0));
				target.getWorld().spawnParticle(Particle.CLOUD, target.getLocation(), 40);
			}, 1L);

		}
	}
	
}
