package me.dakto101.skill.unarmed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
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
import me.dakto101.util.Utils;

public class DamMoc extends Skill {

	public DamMoc() {
		super(SkillEnum.DAM_MOC, Arrays.asList(
				"§7§nKích hoạt:§r§7 Tung cú đấm vào mục tiêu, gây §6(1 + 0.2 X Cấp)§7 sát thương vật lý và ",
				"§7hất tung mục tiêu. Cú đấm được tính như đòn đánh thường, nhận được sát thương",
				"§7cộng thêm và hiệu ứng từ bị động. (Click phải)",
				"",
				"§7§nBị động:§r§7 ",
				"§7- Tăng §6(2.2 + 0.18 X Cấp)§7 sát thương vật lý khi dùng tay không.",
				"§7- Có §f10%§7 cơ hội hất tung mục tiêu và tăng §6(0.4 X Cấp)§7 sát thương vật lý ",
				"§7khi đánh bằng tay không."
				), 10d, SkillType.UNARMED);
		setFoodRequire(5);
		setCooldown(0.3);
		setIcon(Material.RABBIT_FOOT);
	}
	
	@Override
    public List<String> getDescription(int level, final LivingEntity user) {
		List<String> description = new ArrayList<String>(this.getDescription());
    	description.replaceAll(s -> s.replace("(1 + 0.2 X Cấp)", "" + (1 + 0.2 * level)));
    	description.replaceAll(s -> s.replace("(2.2 + 0.18 X Cấp)", "" + (2.2 + 0.18 * level)));
    	description.replaceAll(s -> s.replace("(0.4 X Cấp)", "" + (0.4 * level)));
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
		
		float damage = 1.0f + 0.2f * level;
		
		BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
		s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
			target.setVelocity(target.getVelocity().add(new Vector(0, 0.6, 0)));
		}, 1L);
    	target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 2);
    	target.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, target.getLocation().add(0, 1, 0), 5);
		HCraftDamageSource.damageNormalAttack(user, target, damage);

		//Cooldown
		Cooldown.setCooldown(user.getUniqueId(), getCooldown(), CooldownType.ACTIVE);
	}
	
	//Passive
	@Override
	public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (user.getEquipment().getItemInMainHand().getType().equals(Material.AIR)) {
			if (!e.getCause().equals(DamageCause.ENTITY_ATTACK)) return;
			//Passive1
			double bonusDamage1 = 2.2 + 0.18 * level;
			e.setDamage(e.getDamage() + bonusDamage1);
			//Passive2
			double chance = 0.1;
			double bonusDamage2 = 0.4 * level;
			if (Math.random() < chance) {
				BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
				s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
					target.setVelocity(target.getVelocity().add(new Vector(0, 0.63, 0)));
				}, 1L);
		    	target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 1);
		    	target.getWorld().spawnParticle(Particle.SWEEP_ATTACK, target.getLocation().add(0, 1, 0), 5);
		    	e.setDamage(e.getDamage() + bonusDamage2);
			}
			
		}
	}
	
}
