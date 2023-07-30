package me.dakto101.skill.magic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import me.dakto101.HCraftEnchantment;
import me.dakto101.api.Cooldown;
import me.dakto101.api.Cooldown.CooldownType;
import me.dakto101.api.Skill;
import me.dakto101.api.SkillEnum;
import me.dakto101.api.SkillType;
import me.dakto101.util.HCraftDamageSource;
import me.dakto101.util.ParticleEffect;
import me.dakto101.util.Utils;

public class BomHenGio extends Skill {

	public BomHenGio() {
		super(SkillEnum.BOM_HEN_GIO, Arrays.asList(
				"§7§nKích hoạt:§r§7 Đặt quả bom sẽ kích nổ sau 4 giây vào mục tiêu, gây §9(7.5 + 0.75 X Cấp)§7",
				"§7sát thương nổ cho các mục tiêu xung quanh. (Shift + Click phải)",
				"",
				"§7§nBị động:",
				"§7- Giảm §9(2 + Cấp)§7 sát thương nhận vào từ các vụ nổ.",
				"§7- Gây thêm §9(2.8 + 0.15 X Cấp)§7 sát thương phép khi dùng sách làm vũ khí.",
				"§7- Gây thêm §6(2.6 + 0.15 X Cấp)§7 sát thương vật lý khi dùng sách làm vũ khí."
				), 10d, SkillType.MAGIC);
		setFoodRequire(7);
		setActiveCooldown(10);
		setPassiveCooldown(0.1);
		setIcon(Material.TNT);
		
	}
	
	@Override
    public List<String> getDescription(int level, final LivingEntity user) {
		List<String> description = new ArrayList<String>(this.getDescription());
    	description.replaceAll(s -> s.replace("(7.5 + 0.75 X Cấp)", "" + (float) (7.5 + 0.75 * level)));
    	description.replaceAll(s -> s.replace("(2 + Cấp)", "" + (2 + level)));
		description.replaceAll(s -> s.replace("(2.8 + 0.15 X Cấp)", "" + (float) (2.8 + 0.15 * level)));
		description.replaceAll(s -> s.replace("(2.6 + 0.15 X Cấp)", "" + (float) (2.6 + 0.15 * level)));
    	return description;
    }
	
	//Active
	@Override
	public void applyInteractEntity(final Player user, final int level, final PlayerInteractEntityEvent e) {
		Entity target = e.getRightClicked();
		if (!user.isSneaking()) return;
		if (!(target instanceof LivingEntity)) return;
		if (Cooldown.onCooldown(user.getUniqueId(), CooldownType.ACTIVE_SKILL)) {
			Cooldown.sendMessage(user, this.getName(), CooldownType.ACTIVE_SKILL);
			return;
		}
		if (!Utils.canAttack(user, (LivingEntity) target)) return;
		if (user.getFoodLevel() < getFoodRequire()) {
			user.sendMessage("§cKhông đủ điểm thức ăn!"); 
			return;
		} else {
			user.setFoodLevel(user.getFoodLevel() - getFoodRequire());
		}
		Cooldown.setCooldown(user.getUniqueId(), getPassiveCooldown(), CooldownType.PASSIVE_SKILL);

		active(user, level, e);
		
		Cooldown.setCooldown(user.getUniqueId(), getActiveCooldown(), CooldownType.ACTIVE_SKILL);
	}
	
	//Active
	private void active(final Player user, final int level, final PlayerInteractEntityEvent e) {
		//Param
		double radius = 4;
		float damage = (float) (7.5 + 0.75 * level);
		Entity clicked = e.getRightClicked();
		float timeInSec = 3;
		//Code
		user.getWorld().playSound(user.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
		user.swingMainHand();
		BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
		TNTPrimed tnt = (TNTPrimed) e.getRightClicked().getWorld().spawnEntity(clicked.getLocation(), EntityType.PRIMED_TNT);
		tnt.setYield(0f);
		tnt.setFuseTicks((int) (20 * timeInSec));
		clicked.addPassenger(tnt);
		int task1 = s.scheduleSyncRepeatingTask(HCraftEnchantment.plugin, () -> {
			clicked.getWorld().playSound(clicked.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
			ParticleEffect.createNearbyParticle(((LivingEntity) clicked).getEyeLocation().add(0, 1, 0), 50, Particle.REDSTONE,
					0.25, 0.25, 0.25, new Vector(0, 0, 0), new DustOptions(Color.RED, 1));
		}, 0L, 20L);
		//After 4 seconds...
		s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
			//Cancel task1
			s.cancelTask(task1);
			
			//Explode
			World w = user.getWorld();
			Location loc = clicked.getLocation();
			w.spawnParticle(Particle.EXPLOSION_HUGE, loc, 2);
			w.spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, loc, 100);
			
			//Damage entity task
			w.getNearbyEntities(loc, radius, radius, radius, entity -> (entity instanceof LivingEntity) && 
					(Utils.canAttack(user, (LivingEntity) entity) && (entity.getLocation().distance(loc) < radius)))
			.stream().limit(15).forEach(entity -> {
				entity.setVelocity(entity.getVelocity().add(new Vector(Math.random()*0.5, 0.8 + Math.random()*0.1, Math.random()*0.5)));
				HCraftDamageSource.damageExplosion(user, (LivingEntity) entity, damage);

			});
		}, (long) (20 * timeInSec));
	}
	
	//Passive1
	@Override
    public void applyDefense(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageEvent e) {
    	if (e.getCause().equals(DamageCause.ENTITY_EXPLOSION)) {
    		double reduction = 2 + level;
    		e.setDamage(e.getDamage() - reduction);
    	}
    }
	
	//Passive 2 3
	@Override
	public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (this.getMaterialList().contains(user.getEquipment().getItemInMainHand().getType())) {
			if (Cooldown.onCooldown(user.getUniqueId(), CooldownType.PASSIVE_SKILL)) return;
			if (e.getCause().equals(DamageCause.ENTITY_ATTACK)) {
				Cooldown.setCooldown(user.getUniqueId(), getPassiveCooldown(), CooldownType.PASSIVE_SKILL);
				// Passive 2
				float magicDamage = (float) (2.8 + 0.15 * level);

				HCraftDamageSource.damageIndirectMagic(user, target, magicDamage);
				target.getWorld().spawnParticle(Particle.SPELL_WITCH, target.getEyeLocation(), (int) (10 + magicDamage * 2));
				// Passive 3
				float meleeDamage = (float) (2.6 + 0.15 * level);
				HCraftDamageSource.damageNormalAttack(user, target, meleeDamage);

			}
		}
	}
	
}
