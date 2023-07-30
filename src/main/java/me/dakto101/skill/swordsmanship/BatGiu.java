package me.dakto101.skill.swordsmanship;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

public class BatGiu extends Skill {

	private static final HashMap<LivingEntity, Integer> STACK = new HashMap<LivingEntity, Integer>();
	
	public BatGiu() {
		super(SkillEnum.BAT_GIU, Arrays.asList(
				"§7§nKích hoạt:§r§7 Kéo mục tiêu lại, gây sát thương vật lý §6(6 + 0.72 X Cấp) §7và trói mục tiêu trong 3 ",
				"§7giây. (Shift + Click phải)",
				"",
				"§7§nBị động:",
				"§7- Mỗi 3 lần dùng kỹ năng hoặc đòn đánh bằng kiếm hoặc rìu, gây thêm sát thương vật lý bằng",
				"§7§60.4 X Cấp + Bonus §7(§6Bonus§7 = 6% máu hiện tại của mục tiêu (bonus tối đa = §66§7 sát thương))."
				), 10d, SkillType.SWORDSMANSHIP);
		setFoodRequire(2);
		setActiveCooldown(8);
		setIcon(Material.LEAD);
	}
	
	@Override
    public List<String> getDescription(int level, final LivingEntity user) {
		List<String> description = new ArrayList<String>(this.getDescription());
    	description.replaceAll(s -> s.replace("(6 + 0.72 X Cấp)", "" + (6 + 0.72 * level)));
    	description.replaceAll(s -> s.replace("0.4 X Cấp", "" + (0.4 * level)));
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
		//Param
		float damage = 6.0f + 0.72f * level;
		Vector v = user.getEyeLocation().getDirection().multiply(-1);
		BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
		int duration = 3 * 20;
		//Code
		
    	s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
			user.swingMainHand();
			target.setVelocity(v);
			target.getWorld().playSound(target.getLocation(), Sound.ITEM_TRIDENT_HIT, 1, 0);
			target.getWorld().playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 0);
	    	target.getWorld().spawnParticle(Particle.SWEEP_ATTACK, target.getLocation().add(0, 1, 0), 3);
	    	target.getWorld().spawnParticle(Particle.SMOKE_LARGE, target.getLocation().add(0, 1, 0), 0, 0, 1, 0);
	    	target.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, target.getLocation().add(0, 1, 0), 10);
	    	HCraftDamageSource.damageNormalAttack(user, target, damage);
	    	target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, 10, false, false, false));
		}, 1L);
		
		//Cooldown
		Cooldown.setCooldown(user.getUniqueId(), getActiveCooldown(), CooldownType.ACTIVE_SKILL);
	}
	
	//Passive
	@Override
	public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (!e.getCause().equals(DamageCause.ENTITY_ATTACK)) return;
		if (e.getDamage() < 4) return;
		if (this.getMaterialList().contains(user.getEquipment().getItemInMainHand().getType())) {
			//Param
			double maxHealth = target.getHealth() > 100 ? 100 : target.getHealth();
			double bonusDamage = maxHealth * 0.06 + 0.4 * level;
			//Code
    		if (STACK.size() > 100) STACK.clear();
    		STACK.putIfAbsent(user, 0);
    		STACK.replace(user, STACK.get(user) + 1);
    		if (STACK.get(user) == 3) {
    			STACK.remove(user);
    			target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 1);
    			e.setDamage(e.getDamage() + bonusDamage);
    		}
    	}
	}
	
	
}
