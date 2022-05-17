package me.dakto101.skill.archery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import me.dakto101.HCraftEnchantment;
import me.dakto101.api.Cooldown;
import me.dakto101.api.Cooldown.CooldownType;
import me.dakto101.api.Skill;
import me.dakto101.api.SkillEnum;
import me.dakto101.api.SkillType;
import me.dakto101.api.Toggle;
import me.dakto101.api.Toggle.ToggleType;
import me.dakto101.util.DamageSourceEnum;
import me.dakto101.util.HCraftDamageSource;
import me.dakto101.util.Utils;

public class CuNhayBungNo extends Skill {

	private static final Map<LivingEntity, Boolean> CHECK = new HashMap<LivingEntity, Boolean>();

	public CuNhayBungNo() {
		super(SkillEnum.CU_NHAY_BUNG_NO, Arrays.asList(
				"§7§nKích hoạt:§r§7 Bạn sẽ theo hướng của mũi tên, phát nổ khi tiếp đất gây ",
				"§7§9(2 + 0.2 X Cấp)§7 sát thương phép. (Shift + Click trái)",
				"",
				"§7§nBị động:",
				"§7- Mũi tên bắn ra gây thêm §66% + 2% X Cấp§7 sát thương vật lý."
				), 10d, SkillType.ARCHERY);
		setFoodRequire(15);
		setCooldown(60);
		setIcon(Material.FIREWORK_ROCKET);
		
	}
	
	@Override
    public List<String> getDescription(int level, final LivingEntity user) {
		List<String> description = new ArrayList<String>(this.getDescription());
    	description.replaceAll(s -> s.replace("(2 + 0.2 X Cấp)", "" + (3 + 0.3 * level)));
    	description.replaceAll(s -> s.replace("6% + 2% X Cấp", "" + (6 + level * 2) + "%"));
    	return description;
    }
	
	//Active
	@Override
	public void applyInteractBlock(final Player user, final int level, final PlayerInteractEvent e) { 
		if (e.getAction().equals(Action.LEFT_CLICK_AIR) || 
				e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			if (!user.isSneaking()) return;
			boolean toggle = Toggle.getToggle(user.getUniqueId(), ToggleType.ACTIVE_SKILL);
			Toggle.setToggle(user.getUniqueId(), !toggle, ToggleType.ACTIVE_SKILL);
			Toggle.sendMessage(user, ToggleType.ACTIVE_SKILL);
			user.playSound(user.getLocation(), Sound.UI_BUTTON_CLICK, 1, toggle ? 0.5f : 0.7f);
		}
	}
	
	//Active
	@Override
    public void applyProjectile(final LivingEntity u, final int level, final EntityShootBowEvent e) {
		//Condition
		Player user = (Player) u;
		if (!(user instanceof Player)) return;
		if (!Toggle.getToggle(user.getUniqueId(), ToggleType.ACTIVE_SKILL)) return;
		if (Cooldown.onCooldown(user.getUniqueId(), CooldownType.ACTIVE)) {
			Cooldown.sendMessage(user, this.getName(), CooldownType.ACTIVE);
			return;
		}
		if (user.getFoodLevel() < getFoodRequire()) {
			user.sendMessage("§cKhông đủ điểm thức ăn!"); 
			return;
		}
		if (e.getForce() < 0.9) return;
		//Param
		//Code
		active(user, level, e);
		//Cooldown and food
		Cooldown.setCooldown(user.getUniqueId(), getCooldown(), CooldownType.ACTIVE);
		user.setFoodLevel(user.getFoodLevel() - getFoodRequire());
	}
	
	//Active
	private void active(final LivingEntity user, final int level, final EntityShootBowEvent e) {
		//Param
		float damage = (float) (2 + 0.2 * level);
		double radius = 4;
		Entity a = e.getProjectile();
		World w = user.getWorld();
		
		//Code
		a.addPassenger(user);
		a.setVelocity(a.getVelocity().multiply(0.5));
		
		CHECK.putIfAbsent(user, true);
		w.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, user.getLocation(), 300);
		w.createExplosion(user.getLocation(), 0.01f, false, false, user);
		w.spawnParticle(Particle.EXPLOSION_LARGE, user.getLocation(), 5);
		
		BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
		int taskID = s.scheduleSyncRepeatingTask(HCraftEnchantment.plugin, () -> {
			if (!a.isDead() && !a.isOnGround()) {
				w.spawnParticle(Particle.LAVA, a.getLocation(), 10);
				w.spawnParticle(Particle.CLOUD, a.getLocation(), 20);
			} else {
				if (CHECK.get(user) != null) {
					w.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, a.getLocation(), 300);
					w.getNearbyEntities(a.getLocation(), radius, radius, radius, entity -> (entity instanceof LivingEntity) && 
							(Utils.canAttack(user, (LivingEntity) entity) && (entity.getLocation().distance(a.getLocation()) < radius)))
					.stream().limit(10).forEach(entity -> {
						entity.setVelocity(entity.getVelocity().add(new Vector(Math.random()*0.5, 0.8 + Math.random()*0.1, Math.random()*0.5)));
						HCraftDamageSource.damage(user, (LivingEntity) entity, DamageSourceEnum.MAGIC, damage);
					});
					w.spawnParticle(Particle.EXPLOSION_LARGE, a.getLocation(), 5);
					w.createExplosion(a.getLocation(), 0f, false, false);
					CHECK.remove(user);
				}
			}
		}, 5L, 2L);
		
		s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
			s.cancelTask(taskID);
			CHECK.remove(user);
		}, 200L);
	}
	
	//Passive 
	@Override
    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (e.getCause().equals(DamageCause.PROJECTILE) && e.getDamager() instanceof AbstractArrow) {
			//Param
			double bonus = 0.06 + 0.02 * level;
			double base = e.getDamage(DamageModifier.BASE);
			//Code
			e.setDamage(base + base * bonus);
		}
		
	}
}
