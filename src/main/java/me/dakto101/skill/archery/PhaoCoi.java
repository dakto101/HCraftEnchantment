package me.dakto101.skill.archery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
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

public class PhaoCoi extends Skill {
	
	private static String PHAO_COI = "§6§lĐạn cối";

	public PhaoCoi() {
		super(SkillEnum.PHAO_COI, Arrays.asList(
				"§7§nKích hoạt:§r§7 Bắn ra đạn cối gây §6(3 + 0.26 X Cấp)§7 sát thương vật lý và nổ tung, ",
				"§7gây §9(4 + 0.33 X Cấp)§7 sát thương nổ lên mục tiêu xung quanh. §7(Shift + Click trái)",
				"",
				"§7§nBị động:",
				"§7- Có §f6%§7 cơ hội tạo một vụ nổ xung quanh mục tiêu, gây §9(3 + 0.28 X Cấp)§7",
				"§7sát thương nổ."
				), 10d, SkillType.ARCHERY);
		setFoodRequire(8);
		setCooldown(30);
		setIcon(Material.FIRE_CHARGE);
		
	}
	
	@Override
    public List<String> getDescription(int level, final LivingEntity user) {
		List<String> description = new ArrayList<String>(this.getDescription());
    	description.replaceAll(s -> s.replace("(3 + 0.26 X Cấp)", "" + (3 + 0.26 * level)));
    	description.replaceAll(s -> s.replace("(4 + 0.33 X Cấp)", "" + (4 + 0.33 * level)));
    	description.replaceAll(s -> s.replace("(3 + 0.28 X Cấp)", "" + (3 + 0.28 * level)));
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
		if (e.getForce() < 0.5) return;
		//Param
		//Code
		Entity proj = e.getProjectile();
		Snowball bullet = (Snowball) user.getWorld().spawnEntity(e.getProjectile().getLocation(), EntityType.SNOWBALL);
		//e.setProjectile(bullet);
		e.setCancelled(true);
		
		bullet.setCustomName(PHAO_COI);
		bullet.setCustomNameVisible(true);
		bullet.setFireTicks(80);
		bullet.setVelocity(proj.getVelocity().multiply(0.5));
		bullet.setGlowing(true);
		bullet.setShooter(user);
		bullet.setGravity(false);
		BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
		s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
			bullet.remove();
		}, 200);
		
		user.setVelocity(proj.getVelocity().multiply(-0.25));
		user.getWorld().playSound(user.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 3, 0.75f);
		//Cooldown and food
		Cooldown.setCooldown(user.getUniqueId(), getCooldown(), CooldownType.ACTIVE);
		user.setFoodLevel(user.getFoodLevel() - getFoodRequire());
		
	}
	
	//Active
	@Override
    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (!(target instanceof LivingEntity)) return;
		//Active
		if (e.getCause().equals(DamageCause.PROJECTILE)) {
			String name = e.getDamager().getCustomName();
			if (name != null && name.equals(PHAO_COI)) {
				//Param
				double normalDmg = 3 + 0.26 * level;
				float blastDmg = (float) (4 + 0.33 * level);
				double radius = 5;
				//Code
				e.setDamage(normalDmg);
				target.getWorld().getNearbyEntities(target.getLocation(), radius, radius, radius, 
						entity -> (entity instanceof LivingEntity) && 
						(Utils.canAttack(user, (LivingEntity) entity) && 
						(entity.getLocation().distance(target.getLocation()) < radius)))
				.stream().limit(15).forEach(entity -> {
					entity.setVelocity(entity.getVelocity().add(new Vector(Math.random()*0.5, 0.8 + Math.random()*0.1, Math.random()*0.5)));
					HCraftDamageSource.damage(user, (LivingEntity) entity, DamageSourceEnum.IN_FIRE, blastDmg);
				});
				target.getWorld().playSound(target.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 2, 2);
				target.getWorld().spawnParticle(Particle.FLAME, target.getLocation(), 500);
				target.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, target.getLocation(), 100);
			}
		//Passive
			if (e.getDamager() instanceof Arrow) {
				//Param
				double chance = 0.06;
				float dmg = (float) (3 + 0.28 * level);
				double radius = 5;
				//Code
				if (Math.random() < chance) {
					World w = target.getWorld();
					Location loc = target.getLocation();
					w.spawnParticle(Particle.EXPLOSION_HUGE, loc, 2);
					w.spawnParticle(Particle.SMOKE_LARGE, loc, 100);
					w.playSound(target.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 2, 0);
					//Explode
					w.getNearbyEntities(target.getLocation(), radius, radius, radius, 
							entity -> (entity instanceof LivingEntity) && 
							(Utils.canAttack(user, (LivingEntity) entity) && 
							(entity.getLocation().distance(target.getLocation()) < radius)))
					.stream().limit(15).forEach(entity -> {
						entity.setVelocity(entity.getVelocity().add(new Vector(Math.random()*0.5, 0.8 + Math.random()*0.1, Math.random()*0.5)));
						HCraftDamageSource.damage(user, (LivingEntity) entity, DamageSourceEnum.IN_FIRE, dmg);
					});
					
				}
			}
		}
		
	}
}
