package me.dakto101.skill.unarmed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import me.dakto101.api.Cooldown;
import me.dakto101.api.Cooldown.CooldownType;
import me.dakto101.api.Skill;
import me.dakto101.api.SkillEnum;
import me.dakto101.api.SkillType;
import me.dakto101.util.HCraftDamageSource;
import me.dakto101.util.ParticleEffect;
import me.dakto101.util.Utils;

public class DiaChan extends Skill {

	public DiaChan() {
		super(SkillEnum.DIA_CHAN, Arrays.asList(
				"§7§nKích hoạt:§r§7 Vận nội công xuống đất để tạo một cơn chấn động gây làm ",
				"§7chậm và §9(8 + 0.5 X Cấp)§7 sát thương va đập lên các mục tiêu xung quanh. ",
				"§7Sát thương được tăng thành §9(12 + 0.5 X Cấp)§7 nếu mục tiêu không phải là người",
				"§7chơi. Gây thêm hiệu ứng Làm Chậm I và Suy Yếu I trong 3 giây.",
				"§7(Shift + Click phải)",
				"",
				"§7§nBị động:§r§7 ",
				"§7- Giảm §9(0.5 + 0.5 X Cấp)§7 sát thương va đập nhận vào (khi bị rơi, địa chấn...).",
				"§7- Tăng §6(4 + 0.3 X Cấp)§7 sát thương vật lý khi dùng tay không."
				), 10d, SkillType.UNARMED);
		setFoodRequire(6);
		setActiveCooldown(10);
		setIcon(Material.CRACKED_STONE_BRICKS);
	}
	
	@Override
    public List<String> getDescription(int level, final LivingEntity user) {
		List<String> description = new ArrayList<String>(this.getDescription());
    	description.replaceAll(s -> s.replace("(8 + 0.5 X Cấp)", "" + (8 + 0.5 * level)));
    	description.replaceAll(s -> s.replace("(12 + 0.5 X Cấp)", "" + (12 + 0.5 * level)));
    	description.replaceAll(s -> s.replace("(0.5 + 0.5 X Cấp)", "" + (0.5 + 0.5 * level)));
    	description.replaceAll(s -> s.replace("(4 + 0.3 X Cấp)", "" + (4 + 0.3 * level)));
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
			diaChan(user, level);

			Cooldown.setCooldown(user.getUniqueId(), getActiveCooldown(), CooldownType.ACTIVE_SKILL);

		}
	}
	
	//Passive1
	@Override
	public void applyDefense(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageEvent e) {
		if (!e.getCause().equals(DamageCause.FALL)) return;
		double reduction = 0.5 + 0.5 * level;
		e.setDamage(e.getDamage() - reduction);
	}
	
	//Passive2
	@Override
	public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (user.getEquipment().getItemInMainHand().getType().equals(Material.AIR)) {
			if (!e.getCause().equals(DamageCause.ENTITY_ATTACK)) return;
			double bonusDamage = 4 + 0.3 * level;
			e.setDamage(e.getDamage() + bonusDamage);
		}
	}
	
	private void diaChan(final Player user, final int level) {
		//Param
		double radius = 5;
		float playerDamage = (float) (8 + level * 0.5);
		float mobDamage = (float) (12 + level * 0.5);

		//Particle
		for (double i = 1; i <= radius; i+=1) {
			ParticleEffect.createCircleEffect(user.getLocation(), 30, i, Particle.EXPLOSION_LARGE, new Vector(0, 0.5, 0), null);
			ParticleEffect.createCircleEffect(user.getLocation(), 10, i, Particle.SMOKE_LARGE, new Vector(0, 0.2, 0), null);
		}
		World w = user.getWorld();
		Location loc = user.getLocation();
		user.swingMainHand();
		w.spawnParticle(Particle.SMOKE_LARGE, loc, 50);
		w.playEffect(user.getLocation().add(0, 1, 0), Effect.MOBSPAWNER_FLAMES, 2);
		w.playSound(loc, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1, 0);
		w.playSound(loc, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1, 0);
		//Damage entity task
		w.getNearbyEntities(loc, radius, 2, radius, entity -> (entity instanceof LivingEntity) && 
				(Utils.canAttack(user, (LivingEntity) entity) && (entity.getLocation().distance(loc) < 5)))
		.forEach(entity -> {
				HCraftDamageSource.damageFall(user, (LivingEntity) entity, (entity instanceof Player) ? playerDamage : mobDamage);
				entity.setVelocity(new Vector(0, 0.9, 0));
			((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 0, true, true, true));
			((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 0, true, true, true));
		});
		
	}
	
}
