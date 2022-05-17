package me.dakto101.skill.magic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import me.dakto101.HCraftEnchantment;
import me.dakto101.api.Cooldown;
import me.dakto101.api.Cooldown.CooldownType;
import me.dakto101.api.Skill;
import me.dakto101.api.SkillEnum;
import me.dakto101.api.SkillType;
import me.dakto101.util.DamageSourceEnum;
import me.dakto101.util.HCraftDamageSource;
import me.dakto101.util.ParticleEffect;

@SuppressWarnings("deprecation")
public class ChoSan extends Skill {
	
	private static final String WOLF_NAME = "§b§lChó săn";
	private static final String BABYWOLF_NAME = "§f§lChó săn nhỏ";

	public ChoSan() {
		super(SkillEnum.CHO_SAN, Arrays.asList(
				"§7§nKích hoạt:§r§7 Triệu hồi §f1 §bChó săn§7 và §f3 Chó săn nhỏ §7trong §f25§7 giây.  (Shift + Click phải)",
				"",
				"§7- Chỉ số của §bChó săn§7:",
				"§7+ Máu: §c30 (+ 6.5 X Cấp)",
				"§7+ Sát thương: §63.2 (+ 0.65 X Cấp)",
				"§7+ Giáp: §e2",
				"",
				"§7- Chỉ số của §fChó săn nhỏ§7:",
				"§7+ Máu: §c10",
				"§7+ Sát thương: §62",
				"§7+ Giáp: §e1",
				"",
				"§7§nBị động:",
				"§7- Giảm §610%§7 sát thương vật lý từ quái.",
				"§7- Gây thêm §90.85 + 0.09 X Cấp§7 sát thương phép khi dùng sách làm vũ khí."
				), 10, SkillType.MAGIC);
		setFoodRequire(15);
		setCooldown(60);
		setIcon(Material.WOLF_SPAWN_EGG);
	}
	
	@Override
    public List<String> getDescription(int level, final LivingEntity user) {
		List<String> description = new ArrayList<String>(this.getDescription());
    	description.replaceAll(s -> s.replace("30 (+ 6.5 X Cấp)", "" + (30 + 6.5 * level)));
    	description.replaceAll(s -> s.replace("3.2 (+ 0.65 X Cấp)", "" + (3.2 + 0.65 * level)));
    	description.replaceAll(s -> s.replace("0.85 + 0.09 X Cấp", "" + (0.85 + 0.09 * level)));
    	return description;
    }
	
	//Active
	@Override
	public void applyInteractBlock(final Player user, final int level, final PlayerInteractEvent e) { 
		if (!user.isSneaking()) return;
		if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || 
				e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			//Condition
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
			//Param
			Location loc = e.getClickedBlock().getLocation().add(0, 1, 0);
			//Code
			spawnWolf(user, loc, level);
			spawnWolf(user, loc.add(Math.random() * 6 - 3, 0, Math.random() * 6 - 3), 0);
			spawnWolf(user, loc.add(Math.random() * 6 - 3, 0, Math.random() * 6 - 3), 0);
			spawnWolf(user, loc.add(Math.random() * 6 - 3, 0, Math.random() * 6 - 3), 0);
			
			Cooldown.setCooldown(user.getUniqueId(), getCooldown(), CooldownType.ACTIVE);
		}
		
	}
	
	//Active
	private void spawnWolf(final LivingEntity user, final Location loc, int level) {
		//Param
		double health = level == 0 ? 10 : 30 + 6.5 * level;
		double speed = 0.4;
		double damage = level == 0 ? 2 : 3.2 + 0.65 * level;
		double armor = level == 0 ? 1 : 2;
		long duration = 20 * 25;
		//Code
		
		Wolf wolf = (Wolf) user.getWorld().spawnEntity(loc, EntityType.WOLF);
		wolf.setCollarColor(DyeColor.GREEN);
		wolf.setOwner((AnimalTamer) user);
		wolf.setCustomName(level == 0 ? BABYWOLF_NAME : WOLF_NAME + " §f§l[§7§lLv." + level + "§f§l]");
		wolf.setCustomNameVisible(true);
		wolf.setTamed(true);
		wolf.setAngry(true);
		wolf.setBreed(false);
		wolf.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
		wolf.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
		wolf.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(damage);
		wolf.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(armor);
		wolf.setHealth(health);
		if (level == 0) {
			wolf.setBaby();
		}
		
		ParticleEffect.createNearbyParticle(wolf.getLocation(), 200, Particle.ENCHANTMENT_TABLE, 2, 3, 2, new Vector(0, 0.2, 0), null);
		wolf.getWorld().playSound(wolf.getLocation(), Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1, 0);
		
		BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
		//Remove wolf after 15s.
		s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
			if (wolf.isDead()) return;
			ParticleEffect.createNearbyParticle(wolf.getLocation(), 200, Particle.CLOUD, 2, 3, 2, new Vector(0, 0.2, 0), null);
			wolf.getWorld().playSound(wolf.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 0);
			wolf.remove();
		}, duration);
	}
	
	
	//Passive1
	@Override
    public void applyDefense(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageEvent e) {
    	if (target != null && !(target instanceof Player)) {
    		double reduction = e.getDamage(DamageModifier.BASE) * 0.1;
    		e.setDamage(e.getDamage() - reduction);
    	}
    }
	
	//Passive2
	@Override
	public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (this.getMaterialList().contains(user.getEquipment().getItemInMainHand().getType())) {
			if (e.getCause().equals(DamageCause.ENTITY_ATTACK)) {
				float damage = (float) (0.85 + 0.09 * level);
				HCraftDamageSource.damage(user, target, DamageSourceEnum.MAGIC, damage);
				target.getWorld().spawnParticle(Particle.SPELL_WITCH, target.getEyeLocation(), (int) (damage*10));
			}
		}
	}
	
}
