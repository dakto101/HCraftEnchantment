package me.dakto101.skill.unarmed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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

public class DiemHuyet extends Skill {

	public DiemHuyet() {
		super(SkillEnum.DIEM_HUYET, Arrays.asList(
				"§7§nKích hoạt:§r§7 Điểm huyệt mục tiêu, gây §6(1 + 0.3 X Cấp)§7 sát thương vật lý và bất động ",
				"§7mục tiêu trong §f(0.6 + 0.1 X Cấp)§7 giây. Kĩ năng được tính như đòn đánh thường, gây ",
				"§7các hiệu ứng đi kèm. (Shift + Click phải)",
				"",
				"§7§nBị động:§r§7 ",
				"§7- Tăng §6(2.6 + 0.2 X Cấp)§7 sát thương vật lý khi dùng tay không.",
				"§7- Có §f8%§7 cơ hội gây thêm §6(3 + 0.2 X Cấp)§7 sát thương vật lý khi dùng tay không."
				), 10d, SkillType.UNARMED);
		setFoodRequire(4);
		setCooldown(5);
		setIcon(Material.WITHER_ROSE);
	}
	
	@Override
    public List<String> getDescription(int level, final LivingEntity user) {
		List<String> description = new ArrayList<String>(this.getDescription());
    	description.replaceAll(s -> s.replace("(1 + 0.3 X Cấp)", "" + (1 + 0.3 * level)));
    	description.replaceAll(s -> s.replace("(0.6 + 0.1 X Cấp)", "" + (0.6 + 0.1 * level)));
    	description.replaceAll(s -> s.replace("(2.6 + 0.2 X Cấp)", "" + (2.6 + 0.2 * level)));
    	description.replaceAll(s -> s.replace("(3 + 0.2 X Cấp)", "" + (3 + 0.2 * level)));
    	return description;
    }
	
	//Active
	@Override
	public void applyInteractEntity(final Player user, final int level, final PlayerInteractEntityEvent e) {
		//Condition
		if (!user.isSneaking()) return;
		Entity entity = e.getRightClicked();
		if (!(entity instanceof LivingEntity)) return;
		LivingEntity target = (LivingEntity) entity;
		if (!Utils.canAttack(user, target)) return;
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
		
		active(user, level, e);
		
		//Cooldown
		Cooldown.setCooldown(user.getUniqueId(), getCooldown(), CooldownType.ACTIVE);
	}
	
	//Passive
	@Override
	public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (user.getEquipment().getItemInMainHand().getType().equals(Material.AIR)) {
			if (!e.getCause().equals(DamageCause.ENTITY_ATTACK)) return;
			//Passive1
			double bonusDamage1 = 2.6 + 0.2 * level;
			e.setDamage(e.getDamage() + bonusDamage1);
			//Passive2
			double chance = 0.08;
			double bonusDamage2 = 3 + 0.2 * level;
			if (Math.random() < chance) {
		    	target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 1);
		    	target.getWorld().spawnParticle(Particle.SWEEP_ATTACK, target.getLocation().add(0, 1, 0), 5);
		    	e.setDamage(e.getDamage() + bonusDamage2);
			}
			
		}
	}
	
	private void active(final Player user, final int level, final PlayerInteractEntityEvent e) {
		//Param
		LivingEntity target = (LivingEntity) e.getRightClicked();
		Location loc = target.getLocation();
		float damage = (float) (1 + level * 0.3);
		int duration = (int) (20 * (0.6 + 0.1 * level));
		long interval = 1;
		//Code
		HCraftDamageSource.damageNormalAttack(user, target, damage);
		target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, duration, 99, false, false, false));
		target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration, 0, false, false, false));
		target.getWorld().playSound(loc, Sound.ENTITY_BLAZE_HURT, 1, (float) (Math.random() * 0.2 + 0.5));
		target.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, target.getEyeLocation(), 1);
		user.setVelocity(user.getVelocity().add(user.getEyeLocation().getDirection().multiply(0.1)));
		ParticleEffect.createNearbyParticle(target.getEyeLocation(), 10, Particle.SMOKE_LARGE, 1, 1, 1, new Vector(0, 0.2, 0), null);
		
		BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
		int taskID = s.scheduleSyncRepeatingTask(HCraftEnchantment.plugin, () -> {
			target.setVelocity(new Vector());
			target.teleport(loc, TeleportCause.PLUGIN);
		}, 0L, interval);
		s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
			
			s.cancelTask(taskID);
		}, duration);
	}
	
}
