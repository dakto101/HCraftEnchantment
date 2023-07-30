package me.dakto101.enchantment.armor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitScheduler;

import me.dakto101.HCraftEnchantment;
import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentType;
import me.dakto101.util.HCraftDamageSource;
import me.dakto101.util.Utils;

public class TuHuy extends CustomEnchantment {

	public TuHuy() {
		super("Tự hủy", "§7Sau khi chết sẽ xuất hiện vụ nổ chết chóc gây §9(4 + Cấp X 1) §7sát "
				+ "thương nổ. ", 30);
		setCanStack(true);
		setType(CustomEnchantmentType.DEFENSE);
	}
	
	@Override
	public String getDescription(int level, final LivingEntity user) {
		return this.getDescription().replace("(4 + Cấp X 1)", "" + (4 + level * 1));
	}
	
	@Override
    public void applyDeath(final LivingEntity user, final int level, final EntityDeathEvent event) {
		float damage = 4 + level * 1;
		Location loc = user.getLocation();
		World w = user.getWorld();
		w.playSound(loc, Sound.ENTITY_CREEPER_PRIMED, 1, 1);
		
		BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
		s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
			w.getNearbyEntities(loc, 5, 5, 5)
			.forEach(entity -> explode(user, loc, entity, damage));
			w.spawnParticle(Particle.EXPLOSION_HUGE, loc, 2);
			w.createExplosion(loc, 0f, false, false);
		}, 40L);
	}
	
	private void explode(LivingEntity user, Location loc, Entity e, float damage) {
		if (e instanceof LivingEntity) {
			LivingEntity en = (LivingEntity) e;
			if (!Utils.canAttack(user, en)) return;
			try {
				HCraftDamageSource.damageExplosion(user, en, damage);
				
			} catch (Exception exception) {
				Bukkit.getLogger().info("Enchant Tu huy bi loi. User = " + user.getName());
			}
		}
	}
	
}
