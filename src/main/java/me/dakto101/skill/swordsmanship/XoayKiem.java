package me.dakto101.skill.swordsmanship;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import io.netty.util.internal.ThreadLocalRandom;
import me.dakto101.HCraftEnchantment;
import me.dakto101.api.Cooldown;
import me.dakto101.api.Cooldown.CooldownType;
import me.dakto101.api.Skill;
import me.dakto101.api.SkillEnum;
import me.dakto101.api.SkillType;
import me.dakto101.util.HCraftDamageSource;
import me.dakto101.util.Utils;

@SuppressWarnings("deprecation")
public class XoayKiem extends Skill {

	public XoayKiem() {
		super(SkillEnum.XOAY_KIEM, Arrays.asList(
				"§7§nKích hoạt:§r§7 Xoay kiếm xung quanh trong 5 giây, gây §6(2.25 + Cấp X 0.25)§7 sát thương ",
				"§7vật lý cho kẻ địch bị trúng chiêu. (Shift + Click phải)",
				"",
				"§7§nBị động:",
				"§7- Đòn đánh thường có §f1%§7 cơ hội tăng thêm §6200% + 5% X Cấp§7 sát thương vật lý."
				), 10d, SkillType.SWORDSMANSHIP);
		setFoodRequire(6);
		setActiveCooldown(16);
		setIcon(Material.COMPASS);
	}
	
	@Override
    public List<String> getDescription(int level, final LivingEntity user) {
		List<String> description = new ArrayList<>(this.getDescription());
    	description.replaceAll(s -> s.replace("(2.25 + Cấp X 0.25)", "" + (2.25 + level * 0.25)));
    	description.replaceAll(s -> s.replace("200% + 5% X Cấp", "" + (200 + 5 * level) + "%"));
    	return description;
    }
	
	//Active
	@Override
	public void applyInteractBlock(final Player user, final int level, final PlayerInteractEvent e) { 
		if (!user.isSneaking()) return;
		if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || 
				e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			//Condition
			if (Cooldown.onCooldown(user.getUniqueId(), CooldownType.ACTIVE_SKILL)) {
				Cooldown.sendMessage(user, this.getName(), CooldownType.ACTIVE_SKILL);
				return;
			}
			if (user.getFoodLevel() < getFoodRequire()) {
				user.sendMessage("§cKhông đủ điểm thức ăn!");
				return;
			} else {
				user.setFoodLevel(user.getFoodLevel() - getFoodRequire());
			}
			//Code

			
			active(user, level);
			
			//Cooldown
			Cooldown.setCooldown(user.getUniqueId(), getActiveCooldown(), CooldownType.ACTIVE_SKILL);
		}
	}
	
	private void active(final Player user, final int level) {
		Location loc = user.getLocation();
		//Play task
		BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
		int taskID = s.scheduleSyncRepeatingTask(HCraftEnchantment.plugin, () -> {
			
			if (!this.getMaterialList().contains(user.getInventory().getItemInMainHand().getType())) return;
			
			loc.setYaw(Location.normalizeYaw(loc.getYaw() + ThreadLocalRandom.current().nextInt(30, 60)));
			//user.teleport(loc);
			user.getWorld().playSound(user.getLocation(), Sound.ENTITY_PIG_SADDLE, 1, (float) (1.5 + Math.random() * 0.5));
			user.swingMainHand();

			attack(user, level, loc);
		}, 0L, 2L);
		//Cancel task if player is dead
		s.scheduleSyncRepeatingTask(HCraftEnchantment.plugin, () -> {
			if (user.isDead()) s.cancelTask(taskID);
		}, 2L, 2L);
		//Cancel task after 5s
		s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
			s.cancelTask(taskID);
		}, 100L);
	}
	
	private void attack(final Player user, final int level, final Location loc) {
		UUID uuid = user.getUniqueId();
		float damage = (float) (2.25 + 0.25 * level);
		//Location loc = user.getEyeLocation();
		Vector v = loc.getDirection();
		World w = loc.getWorld();
		Location loc2 = loc.clone();
		Location playerLoc = user.getEyeLocation();
		loc2.setX(playerLoc.getX());
		loc2.setY(playerLoc.getY());
		loc2.setZ(playerLoc.getZ());
		loc2.setWorld(playerLoc.getWorld());
		v.multiply(0.4);
		List<UUID> damaged = new ArrayList<UUID>();
		
		for (int i = 0; i < 20; i++) {
			
			w.spawnParticle(Particle.SWEEP_ATTACK, loc2.getX(), loc2.getY() - 0.5, loc2.getZ(), 0, 0, 0, 0);
			loc2.add(v);
			if (!loc2.getBlock().getType().equals(Material.AIR)) break;
			Iterator<Entity> iter = w.getNearbyEntities(loc2, 0.4, 1, 4).iterator();
			while(iter.hasNext()) {
				Entity en = iter.next();
				if ((en instanceof LivingEntity) && !damaged.contains(en.getUniqueId())
						&& !en.getUniqueId().equals(uuid) && Utils.canAttack(user, (LivingEntity) en)) {
					HCraftDamageSource.damageNormalAttack(user, (LivingEntity) en, (en instanceof Player) ? damage * 2 : damage);
					damaged.add(en.getUniqueId());
				}
			}
		}
	}
	
	//Passive
		@Override
	    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
			double chance = 0.01;
			if (Math.random() > chance) return;
			if (!e.getCause().equals(DamageCause.ENTITY_ATTACK)) return;
	    	if (target instanceof LivingEntity) {
	    		double bonusDamage = e.getDamage(DamageModifier.BASE) * (2 + 0.05 * level);
	    		e.setDamage(e.getDamage() + bonusDamage);
	    		target.getWorld().spawnParticle(Particle.LAVA, target.getLocation().add(0, 1, 0), 10);
	    		target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1, 1);
	    	}
		}
	
	
}
