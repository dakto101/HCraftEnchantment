package me.dakto101.skill.swordsmanship;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.player.PlayerInteractEntityEvent;
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
import me.dakto101.util.Utils;

@SuppressWarnings("deprecation")
public class ChienMa extends Skill {
	
	public ChienMa() {
		super(SkillEnum.CHIEN_MA, Arrays.asList(
				"§7§nKích hoạt:§r§7 Hất văng kẻ địch ra phía trước, gây §65 + Cấp X 1.1§7 sát thương vật lý ",
				"§7và làm chậm kẻ địch 2 giây. Nếu đang cưỡi ngựa, sát thương tăng thành §610 + Cấp X 1.1§7 ",
				"§7và nhận thêm lá chắn chặn §e0.4 X Cấp§7 sát thương. (Shift + Click phải)",
				"",
				"§7§nBị động:",
				"§7- Sử dụng kiếm hoặc rìu khi cưỡi ngựa sẽ tăng thêm §620% + Cấp X 3%§7 sát thương vật lý ",
				"§7gây ra.",
				"§7- Ngựa chiến sẽ nhận giảm §f65%§7 sát thương cơ bản nhận vào."
				), 10d, SkillType.SWORDSMANSHIP);
		setFoodRequire(2);
		setActiveCooldown(4);
		setIcon(Material.SADDLE);
	}
	
	@Override
    public List<String> getDescription(int level, final LivingEntity user) {
		List<String> description = new ArrayList<String>(this.getDescription());
    	description.replaceAll(s -> s.replace("5 + Cấp X 1.1", "" + (5 + level * 1.1)));
    	description.replaceAll(s -> s.replace("10 + Cấp X 1.1", "" + (10 + level * 1.1)));
		description.replaceAll(s -> s.replace("20% + Cấp X 3%", "" + (20 + level * 3) + "%"));
		description.replaceAll(s -> s.replace("0.4 X Cấp", "" + (0.4 * level)));
    	return description;
    }
	
	//Active
	@Override
	public void applyInteractEntity(final Player user, final int level, final PlayerInteractEntityEvent e) {
		//Condition
		Entity entity = e.getRightClicked();
		boolean isRidingHorse = user.getVehicle() != null && user.getVehicle() instanceof Horse;
		if (!isRidingHorse && !user.isSneaking()) return;
		if (!(entity instanceof LivingEntity)) return;
		if (entity instanceof Horse) return;
		LivingEntity target = (LivingEntity) entity;
		if (!Utils.canAttack(user, target)) return;
		if (Cooldown.onCooldown(user.getUniqueId(), CooldownType.ACTIVE_SKILL)) {
			return;
		}
		if (user.getFoodLevel() < getFoodRequire()) {
			user.sendMessage("§cKhông đủ điểm thức ăn!"); 
			return;
		} else {
			user.setFoodLevel(user.getFoodLevel() - getFoodRequire());
		}
		//Param
		float damage = isRidingHorse ? (10f + level * 1.1f) : (5f + 1.1f) * level;
		double barrier = 0.4 * level;
		
		Vector v = (isRidingHorse) ? ((Horse) user.getVehicle()).getEyeLocation().getDirection() : user.getLocation().getDirection();
		BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
		int duration = 2 * 20;
		//Code
		user.swingMainHand();
		v.setY(isRidingHorse ? 0.8 : 0.3);
		if (isRidingHorse) {
			user.getVehicle().setVelocity(user.getVehicle().getVelocity().add(new Vector(v.getX()*2, v.getY() + 0.3, v.getZ()*2)));
			user.setAbsorptionAmount(user.getAbsorptionAmount() + barrier);
		} else {
			user.setVelocity(new Vector(v.getX(), v.getY(), v.getZ()));
		}
		target.getWorld().playSound(target.getLocation(), Sound.ENTITY_IRON_GOLEM_REPAIR, 1, (float) (Math.random()*0.8));
    	target.getWorld().spawnParticle(Particle.SWEEP_ATTACK, target.getLocation().add(0, 1, 0), 3);
    	for (int i = 0; i < 20; i++) {
    		target.getWorld().spawnParticle(Particle.SMOKE_LARGE, target.getLocation().add(0, 1, 0), 0, Math.random(), 0.5, Math.random());
	    	target.getWorld().spawnParticle(Particle.LAVA, target.getLocation().add(0, 1, 0), 0, Math.random() * 0.2, 0.8, Math.random() * 0.2);
    	}
    	
    	HCraftDamageSource.damageNormalAttack(user, target, damage);
    	target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, 1, false, false, false));
    	s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
			target.setVelocity(target.getVelocity().add(new Vector(v.getX(), isRidingHorse ? v.getY() + 0.3 : v.getY() + 0.05, v.getZ())));

		}, 2L);
		
		//Cooldown
		Cooldown.setCooldown(user.getUniqueId(), getActiveCooldown(), CooldownType.ACTIVE_SKILL);
	}
	
	//Passive
	@Override
    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (!e.getCause().equals(DamageCause.ENTITY_ATTACK) && !e.getCause().equals(DamageCause.ENTITY_SWEEP_ATTACK)) return;
    	if (!this.getMaterialList().contains(user.getEquipment().getItemInMainHand().getType())) return;
    	double damage = e.getDamage(DamageModifier.BASE);
    	//Bonus damage in percent
    	double bonus = 0.2 + 0.03 * level;
    	e.setDamage(e.getDamage() + damage * bonus);
	}
	
	//Passive
	public void applyOnVehicleHit(final LivingEntity user, final LivingEntity userVehicle, final LivingEntity target, final int level, final EntityDamageEvent e) {
		double reduction = e.getDamage(DamageModifier.BASE) * 0.65;
		e.setDamage(e.getDamage() - reduction);
	}
	
}
