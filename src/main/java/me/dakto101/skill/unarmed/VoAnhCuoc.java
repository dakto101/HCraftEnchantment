package me.dakto101.skill.unarmed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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

public class VoAnhCuoc extends Skill {

	public VoAnhCuoc() {
		super(SkillEnum.VO_ANH_CUOC, Arrays.asList(
				"§7§nKích hoạt:§r§7 Tung cú đá chí tử vào mục tiêu, gây §6(8 + 1.5 X Cấp)§7 sát thương vật lý, ",
				"§7hất văng mục tiêu. Cú đá được tính như đòn đánh thường, nhận được sát thương",
				"§7cộng thêm và hiệu ứng từ bị động. (Shift + Click phải)",
				"",
				"§7§nBị động:§r§7 ",
				"§7- Tăng §6(5.4 + 0.15 X Cấp)§7 sát thương vật lý khi dùng tay không.",
				"§7- Hồi máu bằng §c(4 + Cấp)%§7 sát thương gây ra từ kỹ năng và đòn đánh tay không."
				), 10d, SkillType.UNARMED);
		setFoodRequire(20);
		setActiveCooldown(60);
		setIcon(Material.RABBIT_HIDE);
	}
	
	@Override
    public List<String> getDescription(int level, final LivingEntity user) {
		List<String> description = new ArrayList<String>(this.getDescription());
    	description.replaceAll(s -> s.replace("(8 + 1.5 X Cấp)", "" + (float) (8.0 + 1.5 * level)));
    	description.replaceAll(s -> s.replace("(5.4 + 0.15 X Cấp)", "" + (float) (5.4 + 0.15 * level)));
    	description.replaceAll(s -> s.replace("(4 + Cấp)", "" + (float) (4 + level)));
    	return description;
    }
	
	//Active
	//Active
	@Override
	public void applyInteractEntity(final Player user, final int level, final PlayerInteractEntityEvent e) {
		//Condition
		if (!user.isSneaking()) return;
		Entity entity = e.getRightClicked();
		if (!(entity instanceof LivingEntity)) return;
		LivingEntity target = (LivingEntity) entity;
		if (!Utils.canAttack(user, target)) return;
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
		
		float damage = 8 + 1.5f * level;

		user.swingMainHand();
		BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
		s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
			Vector v = user.getEyeLocation().getDirection();
			user.setVelocity(v.setY(0.2));
			target.setVelocity(v.multiply(4 + 0.04 * level).setY(1.5));
		}, 1L);
    	target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 0);
    	target.getWorld().playSound(target.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1, 0);
    	ParticleEffect.createNearbyParticle(target.getEyeLocation(), 10, Particle.EXPLOSION_LARGE, 1, 1, 1, new Vector(0, 0, 0), null);
    	HCraftDamageSource.damageNormalAttack(user, target, damage);
		
		//Cooldown
		Cooldown.setCooldown(user.getUniqueId(), getActiveCooldown(), CooldownType.ACTIVE_SKILL);
		
	}
	
	//Passive
	@Override
	public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (user.getEquipment().getItemInMainHand().getType().equals(Material.AIR)) {
			if (!e.getCause().equals(DamageCause.ENTITY_ATTACK)) return;
			//Passive1
			double bonusDamage1 = 5.4 + 0.13 * level;
			e.setDamage(e.getDamage() + bonusDamage1);
			//Passive2
			double healing = e.getFinalDamage() * (0.04 + 0.01 * level);
			double maxHealth = user.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
			user.setHealth(Math.min(user.getHealth() + healing, maxHealth));
		}
	}
	
}
