package me.dakto101.skill.magic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.player.PlayerInteractEvent;
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
import me.dakto101.util.DamageSourceEnum;
import me.dakto101.util.HCraftDamageSource;
import me.dakto101.util.ParticleEffect;
import me.dakto101.util.Utils;

@SuppressWarnings("deprecation")
public class CuongPhong extends Skill {
	
	public CuongPhong() {
		super(SkillEnum.CUONG_PHONG, Arrays.asList(
				"§7§nKích hoạt:§r§7 Biến thành cơn bão trong 5 giây gây §91.8 + 0.32 X Cấp§7 sát thương phép mỗi giây ",
				"§7lên kẻ địch trong bán kính 5 ô và hất tung kẻ địch. (Shift + Click phải)",
				"",
				"§7§nBị động:",
				"§7- Giảm §f6%§7 sát thương vật lý và phép nhận vào.",
				"§7- Gây thêm §90.62 + 0.13 X Cấp§7 sát thương phép khi dùng sách làm vũ khí."
				), 10d, SkillType.MAGIC);
		setFoodRequire(10);
		setCooldown(15);
		setIcon(Material.PHANTOM_MEMBRANE);
	}
	
	@Override
    public List<String> getDescription(int level, final LivingEntity user) {
		List<String> description = new ArrayList<String>(this.getDescription());
    	description.replaceAll(s -> s.replace("1.8 + 0.32 X Cấp", "" + (1.8 + 0.32 * level)));
    	description.replaceAll(s -> s.replace("0.62 + 0.13 X Cấp", "" + (0.62 + 0.13 * level)));
    	return description;
    }
	
	//Active
	@Override
	public void applyInteractBlock(final Player user, final int level, final PlayerInteractEvent e) { 
		if (!user.isSneaking()) return;
		if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || 
				e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			cast(user, level);
		}
	}
	
	//Passive1
	@Override
    public void applyDefense(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageEvent e) {
    	if (e.getCause().equals(DamageCause.MAGIC) || e.getCause().equals(DamageCause.ENTITY_ATTACK)
    			|| e.getCause().equals(DamageCause.PROJECTILE)) {
    		double reduction = e.getDamage(DamageModifier.BASE) * 0.06;
    		e.setDamage(e.getDamage() - reduction);
    	}
    }
	
	//Passive2
	@Override
	public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (this.getMaterialList().contains(user.getEquipment().getItemInMainHand().getType())) {
			if (e.getCause().equals(DamageCause.ENTITY_ATTACK)) {
				float damage = (float) (0.62 + 0.13 * level);
				HCraftDamageSource.damage(user, target, DamageSourceEnum.MAGIC, damage);
				target.getWorld().spawnParticle(Particle.SPELL_WITCH, target.getEyeLocation(), (int) (damage*10));
			}
		}
	}
	
	//Active
	private void cast(final Player user, final int level) {
		//Condition
		if (Cooldown.onCooldown(user.getUniqueId(), CooldownType.ACTIVE)) {
			return;
		}
		user.getWorld().playSound(user.getLocation(), Sound.UI_LOOM_SELECT_PATTERN, 1, 2);
		if (user.getFoodLevel() < getFoodRequire()) {
			user.sendMessage("§cKhông đủ điểm thức ăn!");
			return;
		} else user.setFoodLevel(user.getFoodLevel() - getFoodRequire());
		//Param
		double radius = 5;
		long duration = 20 * 5;
		long interval = 5L;
		float damagePerSec = (float) (1.8 + 0.32 * level);
		//Code
		user.getWorld().playSound(user.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
		user.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) duration, 1, true, true, true));
		//Task
		BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
		int taskID = s.scheduleSyncRepeatingTask(HCraftEnchantment.plugin, () -> {
			Location loc = user.getLocation();
			loc.getWorld().playSound(loc, Sound.ITEM_TRIDENT_RIPTIDE_2, 1, (float) Math.random());
			ParticleEffect.createCircleEffect(loc, 4, radius, Particle.CLOUD, new Vector(0, 0.3, 0), null);
			ParticleEffect.createCircleEffect(user.getEyeLocation().add(0, radius, 0), 6, radius * 0.5, Particle.CLOUD, new Vector(0, -0.6, 0), null);
			ParticleEffect.createVectorParticle(user.getEyeLocation(), 20, Particle.CLOUD, null, 0.5, null, 1, null);
			loc.getWorld().spawnParticle(Particle.CLOUD, user.getEyeLocation().add(0, 1, 0), 30);
			
			user.getNearbyEntities(radius, radius, radius).stream()
			.filter(entity -> entity.getLocation().distance(loc) <= radius && entity instanceof LivingEntity && Utils.canAttack(user, (LivingEntity) entity))
			.limit(15).forEach(entity -> {
				Vector v = entity.getLocation().subtract(loc).toVector().normalize().multiply(0.4);
				v.setY(v.getY() + 0.4);
				entity.setVelocity(v);
				HCraftDamageSource.damage(user, (LivingEntity) entity, DamageSourceEnum.MAGIC, damagePerSec*interval/20);
				entity.getWorld().spawnParticle(Particle.FLAME, entity.getLocation(), 10);
			});
		}, 0L, interval);
		s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
			s.cancelTask(taskID);
			
		}, 100L);
		//Cooldown
		Cooldown.setCooldown(user.getUniqueId(), getCooldown(), CooldownType.ACTIVE);
	}
	
}
