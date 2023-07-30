package me.dakto101.enchantment.ranged;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
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
import me.dakto101.util.HCraftDamageSource;
import me.dakto101.util.Utils;

public class TichDien extends CustomEnchantment {
	
	private static final Map<LivingEntity, Boolean> CHECK = new HashMap<LivingEntity, Boolean>();
	private static final String ARROW_NAME = "§6§2§8§4§2§3§1§7§6Tích điện";
	
	public TichDien() {
		super("Tích điện", "§7Mỗi 80 giây sẽ xuất hiện một mũi tên giật sét kẻ địch "
				+ "xung quanh, gây §9(2 + 2 X Cấp)§7 sát thương nổ và thiêu đốt kẻ "
				+ "địch.", 100);
		setCanStack(false);
		setType(CustomEnchantmentType.RANGED);
	}
	
	@Override
	public String getDescription(int level, final LivingEntity user) {
		return this.getDescription().replace("(2 + 2 X Cấp)", "" + (1 + 0.5 * level));
	}
	
	@Override
    public void applyProjectile(final LivingEntity user, final int level, final EntityShootBowEvent e) {
		
		if (Cooldown.onCooldown(user.getUniqueId(), CooldownType.BOW_ENCHANTMENT)) return;
		
		float damage = 2 + 2 * level;
		Entity a = e.getProjectile();
		a.setCustomName(ARROW_NAME);
		a.setCustomNameVisible(true);
		World w = user.getWorld();
		
		CHECK.putIfAbsent(user, true);
		
		BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
		int taskID = s.scheduleSyncRepeatingTask(HCraftEnchantment.plugin, () -> {
			if (!a.isDead() && !a.isOnGround()) {
				w.spawnParticle(Particle.END_ROD, a.getLocation(), 0, 0, 0, 0);
			} else {
				if (CHECK.get(user) != null) {
					w.strikeLightningEffect(a.getLocation());
					w.getNearbyEntities(a.getLocation(), 5, 10, 5)
					.forEach(entity -> lightning(user, entity, damage));
					CHECK.remove(user);
				}
			}
			
		}, 1L, 1L);
		
		s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
			s.cancelTask(taskID);
			a.remove();
			CHECK.remove(user);
		}, 200L);
		
		Cooldown.setCooldown(user.getUniqueId(), 80, CooldownType.BOW_ENCHANTMENT);
		if (user.getName().equals("dakto101")) 
			Cooldown.setCooldown(user.getUniqueId(), 20, CooldownType.BOW_ENCHANTMENT);
	}
	
	private void lightning(LivingEntity user, Entity e, float damage) {
		if (e instanceof LivingEntity) {
			LivingEntity en = (LivingEntity) e;
			if (!Utils.canAttack(user, en)) return;
			try {
				HCraftDamageSource.damageExplosion(user, en, damage);
				en.setFireTicks(80);
			} catch (Exception exception) {
				Bukkit.getLogger().info("TichDien.java, lightning(LivingEntity, Entity, float) error.");
				if (user.isOp()) user.sendMessage("§cOP: Enchant "
						+ "Tích điện bị lỗi. #lightning");
			}
		}
	}
	
}
