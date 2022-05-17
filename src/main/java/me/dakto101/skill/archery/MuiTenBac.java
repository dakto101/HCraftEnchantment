package me.dakto101.skill.archery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
import me.dakto101.util.DamageSourceEnum;
import me.dakto101.util.HCraftDamageSource;
import me.dakto101.util.ParticleEffect;

public class MuiTenBac extends Skill {
	
	private static String MUI_TEN_BAC = "§7§lMũi tên bạc";

	public MuiTenBac() {
		super(SkillEnum.MUI_TEN_BAC, Arrays.asList(
				"§7§nKích hoạt:§r§7 Kéo tối đa lực bắn sẽ xuất hiện mũi tên bạc gây thêm §f2 + 0.2 X Cấp§7 ",
				"§7sát thương chuẩn và hất tung mục tiêu. Nếu mục tiêu là quái, sát thương chuẩn ",
				"§7gây thêm là §f6 + 0.6 X Cấp§7 và hồi máu bằng §c100%§7 sát thương chuẩn gây thêm bởi ",
				"§7kỹ năng. (Shift + Click trái)",
				"",
				"§7§nBị động:",
				"§7- Phát bắn gây thêm §f1 + 0.1 X Cấp§7 sát thương chuẩn. Tăng lên thành §f3 + 0.3 X Cấp§7 nếu mục tiêu ",
				"§7không phải là người chơi."
				), 10d, SkillType.ARCHERY);
		setFoodRequire(2);
		setCooldown(15);
		setIcon(Material.ARROW);
		
	}
	
	@Override
    public List<String> getDescription(int level, final LivingEntity user) {
		List<String> description = new ArrayList<String>(this.getDescription());
    	description.replaceAll(s -> s.replace("2 + 0.2 X Cấp", "" + (2 + 0.2 * level)));
    	description.replaceAll(s -> s.replace("6 + 0.6 X Cấp", "" + (6 + 0.6 * level)));
    	description.replaceAll(s -> s.replace("1 + 0.1 X Cấp", "" + (1 + 0.1 * level)));
    	description.replaceAll(s -> s.replace("3 + 0.3 X Cấp", "" + (3 + 0.3 * level)));
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
		if (Cooldown.onCooldown(user.getUniqueId(), CooldownType.ACTIVE)) {
			return;
		}
		if (user.getFoodLevel() < getFoodRequire()) {
			user.sendMessage("§cKhông đủ điểm thức ăn!"); 
			return;
		}
		if (e.getForce() < 0.9) return;
		//Param
		//Code
		
		Entity proj = e.getProjectile();
		Arrow bullet = (Arrow) user.getWorld().spawnEntity(e.getProjectile().getLocation(), EntityType.ARROW);
		//e.setProjectile(bullet);
		e.setCancelled(true);
		
		bullet.setCustomName(MUI_TEN_BAC);
		bullet.setCustomNameVisible(true);
		bullet.setVelocity(proj.getVelocity());
		bullet.setGlowing(true);
		bullet.setShooter(user);
		bullet.setPierceLevel((proj instanceof Arrow) ? ((Arrow) proj).getPierceLevel() : 0);
		bullet.setKnockbackStrength((proj instanceof Arrow) ? ((Arrow) proj).getKnockbackStrength() : 0);
		
		
		BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
		s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
			bullet.remove();
		}, 200L);
		
		user.getWorld().playSound(user.getLocation(), Sound.ITEM_TRIDENT_RETURN, 3, 0.75f);
		//Cooldown and food
		Cooldown.setCooldown(user.getUniqueId(), getCooldown(), CooldownType.ACTIVE);
		user.setFoodLevel(user.getFoodLevel() - getFoodRequire());	
	}
	
	//Active
	@Override
    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (!(target instanceof LivingEntity)) return;
		if (e.getFinalDamage() == 0) return;
		//Active
		if (e.getCause().equals(DamageCause.PROJECTILE)) {
			String name = e.getDamager().getCustomName();
			if (name != null && name.equals(MUI_TEN_BAC)) {
				//Param
				double bonusDmg = (target instanceof Player) ? 2 + 0.2 * level : 6 + 0.6 * level;
				double bonusHealth = (target instanceof Player) ? 0 : bonusDmg;
				double maxHealth = user.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
				double health = user.getHealth();
				
				//Code
				HCraftDamageSource.damage(user, (LivingEntity) target, DamageSourceEnum.GENERIC, (float) bonusDmg);
				
				BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
				s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
					target.setVelocity(new Vector(0, 1, 0));
				}, 1L);
				
				user.setHealth((health + bonusHealth >= maxHealth) ? maxHealth : health + bonusHealth);
				if (bonusHealth > 0) {
					ParticleEffect.createNearbyParticle(user.getLocation(), (int) bonusHealth, Particle.HEART, 0, 0, 0, new Vector(0, 0, 0), null);
					user.getWorld().playSound(user.getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 1);
				}
				target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 3, 1.5f);
			}
		//Passive
			//Param
			double bonusDmg = (target instanceof Player) ? 1 + 0.1 * level : 3 + 0.3 * level;
			//Code
			HCraftDamageSource.damage(user, (LivingEntity) target, DamageSourceEnum.GENERIC, (float) bonusDmg);
			ParticleEffect.createNearbyParticle(target.getEyeLocation(), (int) (bonusDmg * 4), Particle.REDSTONE, 1, 1, 1, new Vector(0, 0, 0), new DustOptions(Color.GRAY, 1));
		}
		
	}
}
