package me.dakto101.skill.swordsmanship;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import me.dakto101.HCraftEnchantment;
import me.dakto101.api.Cooldown;
import me.dakto101.api.Cooldown.CooldownType;
import me.dakto101.api.Skill;
import me.dakto101.api.SkillEnum;
import me.dakto101.api.SkillType;
import me.dakto101.util.HCraftDamageSource;

public class GiaoChien extends Skill {
	
	private static final HashMap<LivingEntity, LivingEntity> MARKED = new HashMap<LivingEntity, LivingEntity>();
	private static final HashMap<LivingEntity, Snowball> SNOWBALL_LIST = new HashMap<LivingEntity, Snowball>();
	private static final String SNOWBALL_NAME = "§6Giao chiến";
	
	public GiaoChien() {
		super(SkillEnum.GIAO_CHIEN, Arrays.asList(
				"§7§nKích hoạt:§r§7 Bắn ra quả cầu ma thuật, khi trúng kẻ địch sẽ đánh dấu ",
				"§7trong 5 giây và gây §9(3 + 0.5 X Cấp)§7 sát thương phép. Kẻ địch bị đánh ",
				"§7dấu sẽ nhận thêm §62 X Cấp + 12% máu hiện tại §7 sát thương vật lý từ ",
				"§7đòn đánh cận chiến. (Shift + Click phải)",
				"",
				"§7§nBị động:",
				"§r§7- Khi bị đánh có §f10%§7 nhận được lá chắn hấp thụ §e(3 + 0.5 X Cấp) sát thương."
				), 10d, SkillType.SWORDSMANSHIP);
		setFoodRequire(2);
		setActiveCooldown(8);
		setIcon(Material.SNOWBALL);
	}
	
	@Override
    public List<String> getDescription(int level, final LivingEntity user) {
		List<String> description = new ArrayList<String>(this.getDescription());
    	description.replaceAll(s -> s.replace("(3 + 0.5 X Cấp)", "" + (3 + 0.5 * level)));
    	description.replaceAll(s -> s.replace("2 X Cấp", "" + (2 * level)));
    	description.replaceAll(s -> s.replace("(3 + 0.5 X Cấp)", "" + (3 + 0.5 * level)));
    	return description;
    }
	
	//Active
	@Override
	public void applyInteractBlock(final Player user, final int level, final PlayerInteractEvent e) { 
		if (!user.isSneaking()) return;
		if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || 
				e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
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

			user.swingMainHand();

			World w = user.getWorld();
			Location loc = user.getEyeLocation();

			Snowball ball = (Snowball) w.spawnEntity(loc, EntityType.SNOWBALL);
			ball.setVelocity(loc.getDirection());
			ball.setCustomName(SNOWBALL_NAME);
			ball.setFireTicks(1000);
			ball.setShooter(user);
			ball.setCustomNameVisible(true);
			SNOWBALL_LIST.putIfAbsent(user, ball);
			BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
			s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
				SNOWBALL_LIST.remove(user, ball);
			}, 200L);
			
			w.playSound(loc, Sound.ENTITY_ENDER_DRAGON_SHOOT, 1, 1);
			
			Cooldown.setCooldown(user.getUniqueId(), getActiveCooldown(), CooldownType.ACTIVE_SKILL);
		}
	}
	//Active
	@Override
    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (e.getCause().equals(DamageCause.PROJECTILE)) {
			if (e.getDamager().equals(SNOWBALL_LIST.get(user))) {
				float damage = 3.0f + 0.5f * level;
				SNOWBALL_LIST.remove(user);
				MARKED.putIfAbsent(user, target);
				target.getWorld().playSound(target.getLocation(), Sound.ENTITY_SPLASH_POTION_BREAK, 1, 1);
				target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 1));
				e.setCancelled(true);
				
				//Remove mark after 5s.
				BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
				s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
					MARKED.remove(user, target);
				}, 100L);
				s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
					//
					Cooldown.setCooldown(user.getUniqueId(), 0.1, CooldownType.PASSIVE_SKILL);
					HCraftDamageSource.damageIndirectMagic(user, target, damage);
				}, 1L);
			}
		}
		
		if (e.getCause().equals(DamageCause.ENTITY_ATTACK) || 
				e.getCause().equals(DamageCause.ENTITY_SWEEP_ATTACK)) {
			if (Cooldown.onCooldown(user.getUniqueId(), CooldownType.PASSIVE_SKILL)) return;
			double health = target.getHealth() > 100 ? 100 : target.getHealth();
			double damage = 2 * level + 0.12 * health;

			// Consume mark
			if (MARKED.get(user) != null && MARKED.get(user).equals(target)) {
				user.sendMessage("§6" + this.getName() + "§7 gây thêm §6" + damage + "§7 sát thương vật lý.");
				e.setDamage(e.getDamage() + damage);
				target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 1);
				MARKED.remove(user);
				
			}
			
		}

	}

	//Passive
	@Override
    public void applyDefense(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageEvent event) {
		double chance = 0.1;
		if (Math.random() > chance) return;
    	if (target instanceof LivingEntity) {
    		double absorb = 3 + 0.5 * level;
    		user.setAbsorptionAmount(absorb);
			user.getWorld().playSound(user.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 2, 2);
    	}
    }
	
	
}
