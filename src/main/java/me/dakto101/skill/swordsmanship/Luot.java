package me.dakto101.skill.swordsmanship;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
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
import me.dakto101.util.Utils;

public class Luot extends Skill {

	public Luot() {
		super(SkillEnum.LUOT, Arrays.asList(
				"§7§nKích hoạt: §n§7Lướt một đoạn theo hướng chỉ định, gây §9(1 + 0.1 X Cấp)§7 sát thương ",
				"§7phép cho kẻ địch trúng phải. Sát thương tăng thành §9(1.5 + 0.15 X Cấp)§7 cho quái và mob. Mục ",
				"§7tiêu có thể bị trúng nhiều lần, tối đa 8 lần. (Shift + Click phải)",
				"",
				"§7§nBị động: ",
				"§7- Chặn §6(0.5 + 0.1 X Cấp)§7 sát thương vật lý khi bị tấn công bằng kiếm hoặc rìu."
				), 10d, SkillType.SWORDSMANSHIP);
		setFoodRequire(2);
		setActiveCooldown(6);
		setIcon(Material.WHITE_DYE);
	}
	
	@Override
    public List<String> getDescription(int level, final LivingEntity user) {
		List<String> description = new ArrayList<String>(this.getDescription());
    	description.replaceAll(s -> s.replace("(1 + 0.1 X Cấp)", "" + (1 + 0.1 * level)));
    	description.replaceAll(s -> s.replace("(1.5 + 0.15 X Cấp)", "" + (1.5 + 0.15 * level)));
    	description.replaceAll(s -> s.replace("(0.5 + 0.1 X Cấp)", "" + (0.5 + 0.1 * level)));
    	return description;
    }
	
	//Active
	@Override
	public void applyInteractEntity(final Player user, final int level, final PlayerInteractEntityEvent e) {
		if (!user.isSneaking()) return;
		Entity target = e.getRightClicked();
		if (!(target instanceof LivingEntity)) return;
		if (!Utils.canAttack(user, (LivingEntity) target)) return;
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
		//
		Vector v = user.getEyeLocation().getDirection();
		v.setY(0.02);
		v.multiply(0.5 + 0.02 * level > 1 ? 1 : 0.5 + 0.02 * level);
		Map<UUID, Integer> targetMark = new HashMap<UUID, Integer>();
		float damage = (float) (0.5 + 0.05 * level);
		
		
		BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
		int taskID = s.scheduleSyncRepeatingTask(
				HCraftEnchantment.plugin, () -> dashTask(user, v, targetMark, damage), 1L, 1L);
		
		s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
			s.cancelTask(taskID);
		}, 20L);
		
		Cooldown.setCooldown(user.getUniqueId(), getActiveCooldown(), CooldownType.ACTIVE_SKILL);
	}
	
	//Passive
	@Override
    public void applyDefense(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageEvent e) {
    	if (target == null) return;
    	if (!(target instanceof LivingEntity)) return;
    	if (this.getMaterialList().contains(target.getEquipment().getItemInMainHand().getType())) {
    		double reduction = 0.5 + 0.1 * level;
    		e.setDamage(e.getDamage() - reduction);

    	}
    }
	
	//Active
	private void dashTask(Player user, Vector v, Map<UUID, Integer> target, float damage) {
		user.setVelocity(v);
		if (System.currentTimeMillis() % 2 == 1) user.swingMainHand();
		else user.swingOffHand();
		World w = user.getWorld();
		Location loc = user.getLocation();
		w.spawnParticle(Particle.CLOUD, loc, 50);
		w.playSound(loc, Sound.ENTITY_HORSE_SADDLE, 1, 1);
		w.getNearbyEntities(loc, 1, 1, 1, entity -> (entity instanceof LivingEntity) && 
				(Utils.canAttack(user, (LivingEntity) entity)))
		.forEach(entity -> {
			UUID uuid = entity.getUniqueId();
			target.putIfAbsent(uuid, 0);
			//Toi da 8 phat. X2 damage voi mob.
			target.replace(uuid, target.get(uuid) + 1);
			if (target.get(uuid) <= 8) {
				HCraftDamageSource.damageIndirectMagic(user, (LivingEntity) entity, (entity instanceof Player) ? damage : damage * 1.5f);
			}
		});
	}
	
}
