package me.dakto101.skill.archery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
import me.dakto101.api.Toggle;
import me.dakto101.api.Toggle.ToggleType;
import me.dakto101.util.HCraftDamageSource;
import me.dakto101.util.ParticleEffect;

public class BangTien extends Skill {
	
	private static final String BANG_TIEN = "§b§lBăng tiễn";

	public BangTien() {
		super(SkillEnum.BANG_TIEN, Arrays.asList(
				"§7§nKích hoạt:§r§7 Bắn ra mũi tên băng, gây §9(3.5 + 0.3 X Cấp)§7 sát thương phép ",
				"§7và bất động mục tiêu trong §f(2 + 0.15 X Cấp)§7 giây. (Shift + Click trái)",
				"",
				"§7§nBị động:",
				"§7- Gây thêm §64% + Cấp X 1%§7 sát thương vật lý cho mục tiêu bị làm chậm.",
				"§7- Bắn ra mũi tên băng làm chậm mục tiêu trong §f3§7 giây."
				), 10d, SkillType.ARCHERY);
		setFoodRequire(6);
		setActiveCooldown(30);
		setIcon(Material.ICE);
		
	}
	
	@Override
    public List<String> getDescription(int level, final LivingEntity user) {
    	List<String> description = new ArrayList<String>(this.getDescription());
    	description.replaceAll(s -> s.replace("(3.5 + 0.3 X Cấp)", "" + (3.5 + 0.3 * level)));
    	description.replaceAll(s -> s.replace("(2 + 0.15 X Cấp)", "" + (2 + 0.15 * level)));
    	description.replaceAll(s -> s.replace("4% + Cấp X 1%", "" + (4 + level * 1) + "%"));
    	return description;
    }
	
	
	//Active
	@Override
	public void applyInteractBlock(final Player user, final int level, final PlayerInteractEvent e) { 
		if (e.getAction().equals(Action.LEFT_CLICK_AIR) || 
				e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			if (!user.isSneaking()) return;
			boolean toggle = Toggle.getToggle(user.getUniqueId(), ToggleType.ACTIVE_SKILL);
			Toggle.setToggle(user.getUniqueId(), !toggle, ToggleType.ACTIVE_SKILL);
			Toggle.sendMessage(user, ToggleType.ACTIVE_SKILL);
			user.playSound(user.getLocation(), Sound.UI_BUTTON_CLICK, 1, toggle ? 0.5f : 0.7f);
		}
	}
	
	//Active
	@Override
	public void applyProjectile(final LivingEntity u, final int level, final EntityShootBowEvent e) {
		//Condition
		Player user = (Player) u;
		if (!(user instanceof Player)) return;
		if (!Toggle.getToggle(user.getUniqueId(), ToggleType.ACTIVE_SKILL)) return;
		if (Cooldown.onCooldown(user.getUniqueId(), CooldownType.ACTIVE_SKILL)) {
			return;
		}
		if (user.getFoodLevel() < getFoodRequire()) {
			user.sendMessage("§cKhông đủ điểm thức ăn!"); 
			return;
		}
		//Param
		Entity proj = e.getProjectile();
		World w = proj.getWorld();
		//Code
		
		if (!(proj instanceof AbstractArrow)) return;
		proj.setCustomName(BANG_TIEN);
		proj.setCustomNameVisible(true);
		proj.setGlowing(true);
		proj.getWorld().playSound(proj.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 0);
		
		BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
		int taskID = s.scheduleSyncRepeatingTask(HCraftEnchantment.plugin, () -> {
			if (!proj.isDead()) w.spawnParticle(Particle.REDSTONE, proj.getLocation(), 0, 0, 0, 0, new DustOptions(Color.AQUA, 1));
		}, 0L, 1L);
		s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
			proj.remove();
			s.cancelTask(taskID);
		}, 200L);
		//Cooldown and food
		Cooldown.setCooldown(user.getUniqueId(), getActiveCooldown(), CooldownType.ACTIVE_SKILL);
		user.setFoodLevel(user.getFoodLevel() - getFoodRequire());	
	}
	
	@Override
    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (e.getCause().equals(DamageCause.PROJECTILE)) {
			//Active
			String name = e.getDamager().getCustomName();
			if (name != null && name.equals(BANG_TIEN)) {
				//Param
				Location loc = target.getLocation();
				float damage = (float) (3.5 + level * 0.3);
				int duration = (int) ((2 + 0.15 * level) * 20);
				long interval = 1;
				//Code
				HCraftDamageSource.damageIndirectMagic(user, target, damage);
				target.removePotionEffect(PotionEffectType.WEAKNESS);
				target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, duration, 99, false, false, false));
				target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration, 0, false, false, false));
				target.getWorld().playSound(loc, Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1, 0);
				ParticleEffect.createCircleEffect(target.getEyeLocation(), 2, 1, Particle.REDSTONE, new Vector(), new DustOptions(Color.AQUA, 1));
				
				BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
				int taskID = s.scheduleSyncRepeatingTask(HCraftEnchantment.plugin, () -> {
					target.setVelocity(new Vector());
					target.teleport(loc, TeleportCause.PLUGIN);
				}, 0L, interval);
				s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
					
					s.cancelTask(taskID);
				}, duration);
			}
			//Passive 1
			//Param
			double ratio = 0.04 + level * 0.01;
			//Code
			if (target.hasPotionEffect(PotionEffectType.SLOW)) {
				e.setDamage(e.getDamage() + e.getDamage() * ratio);
				ParticleEffect.createNearbyParticle(target.getEyeLocation(), 40, Particle.REDSTONE, 1, 1, 1, new Vector(), new DustOptions(Color.AQUA, 1));
				target.getWorld().playSound(target.getLocation(), Sound.BLOCK_GLASS_BREAK, 2, 2);
			}
			//Passive 2
			//Param
			int duration = 3 * 20;
			//Code
			target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, 1, true, true, true));
			
		}
	}
}
