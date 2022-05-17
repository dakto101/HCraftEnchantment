package me.dakto101.enchantment.ranged;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitScheduler;

import me.dakto101.HCraftEnchantment;
import me.dakto101.api.Cooldown;
import me.dakto101.api.Cooldown.CooldownType;
import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentType;

public class ThuocNo extends CustomEnchantment {
	
	private static final Map<LivingEntity, Boolean> CHECK = new HashMap<LivingEntity, Boolean>();
	private static final String ARROW_NAME = "§2§3§1§7§6Thuốc nổ";
	
	public ThuocNo() {
		super("Thuốc nổ", "§7Bắn ra một mũi tên chứa thuốc nổ, gây vụ nổ "
				+ "bằng §e(1 + 0.2 X Cấp)§7 đơn vị.", 50);
		setCanStack(false);
		setType(CustomEnchantmentType.RANGED);
		setCooldown(20);
	}
	
	@Override
	public String getDescription(int level, final LivingEntity user) {
		return this.getDescription().replace("(1 + 0.2 X Cấp)", "" + (1 + 0.2 * level));
	}
	
	@Override
    public void applyProjectile(final LivingEntity user, final int level, final EntityShootBowEvent e) {
		
		if (Cooldown.onCooldown(user.getUniqueId(), CooldownType.BOW)) return;
		
		float power = (float) (1 + 0.2 * level);
		Entity a = e.getProjectile();
		a.setCustomName(ARROW_NAME);
		a.setCustomNameVisible(true);
		World w = user.getWorld();
		
		CHECK.putIfAbsent(user, true);
		
		BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
		int taskID = s.scheduleSyncRepeatingTask(HCraftEnchantment.plugin, () -> {
			if (!a.isDead() && !a.isOnGround()) {
				w.spawnParticle(Particle.FLAME, a.getLocation(), 0, 0, 0, 0);
				w.spawnParticle(Particle.FLAME, a.getLocation(), 10, 0, 0, 0);
			} else {
				if (CHECK.get(user) != null) {
					w.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, a.getLocation(), 300);
					w.createExplosion(a.getLocation(), power, false, false, user);
					w.spawnParticle(Particle.EXPLOSION_HUGE, a.getLocation(), 5);
					CHECK.remove(user);
				}
			}
			
		}, 1L, 1L);
		
		s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
			s.cancelTask(taskID);
			a.remove();
			CHECK.remove(user);
		}, 200L);
		
		Cooldown.setCooldown(user.getUniqueId(), getCooldown(), CooldownType.BOW);
	}
	
	
}
