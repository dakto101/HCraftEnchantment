package me.dakto101.skill.magic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import me.dakto101.HCraftEnchantment;
import me.dakto101.api.Cooldown;
import me.dakto101.api.Cooldown.CooldownType;
import me.dakto101.api.Skill;
import me.dakto101.api.SkillEnum;
import me.dakto101.api.SkillType;
import me.dakto101.util.DamageSourceEnum;
import me.dakto101.util.HCraftDamageSource;
import me.dakto101.util.Utils;

public class LucHapDan extends Skill {

	public LucHapDan() {
		super(SkillEnum.LUC_HAP_DAN, Arrays.asList(
				"§7§nKích hoạt:§r§7 Bắn một tia định vị, hất mục tiêu lên cao lên rồi kéo lại. ",
				"§7Gây §9(1.5 + 0.25 X Cấp)§7 sát thương phép. (Shift + Click phải)",
				"",
				"§7§nBị động:",
				"§7- Giảm §9(1 + 0.4 X Cấp)§7 sát thương va đập.",
				"§7- Gây thêm §90.82 + 0.15 X Cấp§7 sát thương phép khi dùng sách làm vũ khí."
				),
				10d, SkillType.MAGIC);
		setFoodRequire(4);
		setCooldown(10);
		setIcon(Material.END_ROD);
	}
	
	@Override
    public List<String> getDescription(int level, final LivingEntity user) {
		List<String> description = new ArrayList<String>(this.getDescription());
    	description.replaceAll(s -> s.replace("(1.5 + 0.25 X Cấp)", "" + (1.5 + 0.25 * level)));
    	description.replaceAll(s -> s.replace("(1 + 0.4 X Cấp)", "" + (1 + 0.4 * level)));
    	description.replaceAll(s -> s.replace("0.82 + 0.15 X Cấp", "" + (0.82 + 0.15 * level)));
    	return description;
    }
	
	//Passive1
	@Override
	public void applyDefense(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageEvent e) {
		if (e.getCause().equals(DamageCause.FALL)) {
			double reduction = 1 + 0.4 * level;
			e.setDamage(e.getDamage() - reduction);
		}
	}
	
	//Passive2
	@Override
	public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (this.getMaterialList().contains(user.getEquipment().getItemInMainHand().getType())) {
			if (e.getCause().equals(DamageCause.ENTITY_ATTACK)) {
				float damage = (float) (0.85 + 0.09 * level);
				HCraftDamageSource.damage(user, target, DamageSourceEnum.MAGIC, damage);
				target.getWorld().spawnParticle(Particle.SPELL_WITCH, target.getEyeLocation(), (int) (damage*10));
			}
		}
	}
	
	//Active
	@Override
	public void applyInteractBlock(final Player user, final int level, final PlayerInteractEvent e) { 
		if (!user.isSneaking()) return;
		if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || 
				e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (Cooldown.onCooldown(user.getUniqueId(), CooldownType.ACTIVE)) {
				Cooldown.sendMessage(user, this.getName(), CooldownType.ACTIVE);
				return;
			}
			if (user.getFoodLevel() < getFoodRequire()) {
				user.sendMessage("§cKhông đủ điểm thức ăn!");
				return;
			} else {
				user.setFoodLevel(user.getFoodLevel() - getFoodRequire());
			}
			//Param
			float damage = (float) (1.5 + 0.25 * level);
			//Code
			Vector check = user.getEyeLocation().getDirection();
			check.multiply(0.25);
			Location loc = user.getEyeLocation().add(check);
			World w = loc.getWorld();
			BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
			loc.getWorld().playSound(user.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1, 1);
			
			for (int i = 0; i < 70 + 10 * level; i++) {
				loc.add(check);
				loc.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, loc, 0, 0, 0, 0);
				Iterator<Entity> iter = w.getNearbyEntities(loc, 0.5, 0.5, 0.5, entity -> entity instanceof LivingEntity && (!entity.equals(user))).iterator();
				while (iter.hasNext()) {
					Entity entity = iter.next();
					if ((entity instanceof LivingEntity) && (entity != null)) {
						final LivingEntity target = (LivingEntity) entity;
						task(user, target, s, damage);
					}
					i = 1000;
					break;
				} 

			}

			Cooldown.setCooldown(user.getUniqueId(), getCooldown(), CooldownType.ACTIVE);
		}
		
	}
	
	private static void task(LivingEntity user, LivingEntity target, BukkitScheduler s, float damage) {
		if (Utils.canAttack(user, target)) {
			s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
				target.setVelocity(new Vector(0, 3, 0));
				target.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, target.getLocation(), 20, 0.5, 0.5, 0.5);
			}, 2L);
			
			Vector v = user.getEyeLocation().getDirection();
			double distance = user.getLocation().distance(target.getLocation());
			v.multiply(-1).multiply(Math.pow(distance, 0.2));
			s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
				target.setVelocity(v);
				HCraftDamageSource.damage(user, target, DamageSourceEnum.MAGIC, damage);
				target.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, target.getLocation(), 20, 0.5, 0.5, 0.5);
			}, 3L);
		}
	}
	
}
