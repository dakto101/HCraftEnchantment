package me.dakto101.skill.swordsmanship;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.util.Vector;

import me.dakto101.api.Cooldown;
import me.dakto101.api.Cooldown.CooldownType;
import me.dakto101.api.Skill;
import me.dakto101.api.SkillEnum;
import me.dakto101.api.SkillType;
import me.dakto101.util.HCraftDamageSource;
import me.dakto101.util.ParticleEffect;
import me.dakto101.util.Utils;

public class CanQuet extends Skill {

	public CanQuet() {
		super(SkillEnum.CAN_QUET, Arrays.asList(
				"§7§nKích hoạt:§r§7 Gây sát thương vật lý §62 + Cấp X 0.2§7 lên các mục tiêu trong",
				"§7bán kính 5 ô. Sát thương tăng thêm §f60%§7 cho mục tiêu không phải người chơi. ",
				"§7(Shift + Click phải)",
				"",
				"§7§nBị động:",
				"§7- Hồi máu bằng §c(4 + Cấp)%§7 sát thương gây ra từ kỹ năng và đòn đánh."
				), 10d, SkillType.SWORDSMANSHIP);
		setFoodRequire(3);
		setCooldown(3);
		setIcon(Material.PHANTOM_MEMBRANE);
	}
	
	@Override
    public List<String> getDescription(int level, final LivingEntity user) {
		List<String> description = new ArrayList<String>(this.getDescription());
    	description.replaceAll(s -> s.replace("2 + Cấp X 0.2", "" + (2 + level * 0.2)));
    	description.replaceAll(s -> s.replace("thêm §f60%", "thành §6" + ((2 + level * 0.2) * 1.6)));
    	description.replaceAll(s -> s.replace("(4 + Cấp)", "" + (4 + level)));
    	return description;
    }
	
	//Active
	@Override
	public void applyInteractEntity(final Player user, final int level, final PlayerInteractEntityEvent e) {
		Entity target = e.getRightClicked();
		//Condition
		if (!user.isSneaking()) return;
		if (!(target instanceof LivingEntity)) return;
		if (!Utils.canAttack(user, (LivingEntity) target)) return;
		if (Cooldown.onCooldown(user.getUniqueId(), CooldownType.ACTIVE)) {
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
		Cooldown.setCooldown(user.getUniqueId(), getCooldown(), CooldownType.ACTIVE);
	}
	
	private void active(final Player user, final int level) {
		//Param
		double radius = 5;
		float damage = (float) (2 + level * 0.2);
		float bonusDamage = damage * 1.6f;

		//Code
		World w = user.getWorld();
		Location loc = user.getLocation();
		w.spawnParticle(Particle.SMOKE_LARGE, loc, 50);
		w.playEffect(user.getLocation().add(0, 1, 0), Effect.MOBSPAWNER_FLAMES, 2);
		w.playSound(loc, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, (float) (Math.random() * 2));
		ParticleEffect.createCircleEffect(user.getLocation().add(0, 1.5, 0), 10, radius, Particle.SWEEP_ATTACK, new Vector(0, 0, 0), null);
		ParticleEffect.createCircleEffect(user.getLocation().add(0, 1.5, 0), 10, radius*0.5, Particle.SWEEP_ATTACK, new Vector(0, 0, 0), null);
			
		//Damage entity task
		w.getNearbyEntities(loc, radius, 2, radius, entity -> (entity instanceof LivingEntity) && 
				(Utils.canAttack(user, (LivingEntity) entity) && (entity.getLocation().distance(loc) < radius)))
		.stream().limit(15).forEach(entity -> {
			Location loc1 = entity.getLocation().add(0, 1, 0);
			w.playSound(loc1, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, (float) (Math.random() * 2));
			w.spawnParticle(Particle.SWEEP_ATTACK, loc1, 2);
			w.spawnParticle(Particle.CLOUD, loc1, 0, 0, 1, 0);
			
			HCraftDamageSource.damageNormalAttack(user, (LivingEntity) entity, (entity instanceof Player) ? damage : damage + bonusDamage);
		});
	}
	
	//Passive
	@Override
	public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (!e.getCause().equals(DamageCause.ENTITY_ATTACK)) return;
		//Passive1
		double healing = e.getFinalDamage() * (0.04 + 0.01 * level);
		double maxHealth = user.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		user.setHealth(user.getHealth() + healing > maxHealth ? maxHealth : user.getHealth() + healing);
	}
	
	
}
