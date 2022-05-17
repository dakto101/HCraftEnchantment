package me.dakto101.skill.swordsmanship;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
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

public class TuBao extends Skill {
	
	private static HashMap<UUID, Integer> stack = new HashMap<UUID, Integer>();

	public TuBao() {
		super(SkillEnum.TU_BAO, Arrays.asList(
				"§7§nKích hoạt:§r§7 Phóng một luồng khí ra phía trước, gây §6(1 + 0.25 X Cấp)§7 sát ",
				"§7thương vật lý cho kẻ địch trúng phải. Khi tích đủ 3 lần sẽ hất ",
				"§7tung kẻ địch. (Click phải)",
				"",
				"§7§nBị động:",
				"§7- Đòn đánh có §f10%§7 tỉ lệ hất tung mục tiêu."
				), 10d, SkillType.SWORDSMANSHIP);
		setFoodRequire(2);
		setCooldown(4);
		setIcon(Material.IRON_SWORD);
	}
	
	@Override
    public List<String> getDescription(int level, final LivingEntity user) {
		List<String> description = new ArrayList<String>(this.getDescription());
    	description.replaceAll(s -> s.replace("(1 + 0.25 X Cấp)", "" + (1 + 0.25 * level)));
    	return description;
    }
	
	//Active
	@Override
	public void applyInteractBlock(final Player user, final int level, final PlayerInteractEvent e) { 
		if (!user.isSneaking()) return;
		if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || 
				e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (Cooldown.onCooldown(user.getUniqueId(), CooldownType.ACTIVE)) {
				return;
			}
			if (user.getFoodLevel() < getFoodRequire()) {
				user.sendMessage("§cKhông đủ điểm thức ăn!");
				return;
			} else {
				user.setFoodLevel(user.getFoodLevel() - getFoodRequire());
			}
			UUID uuid = user.getUniqueId();
			stack.putIfAbsent(uuid, 0);
			stack.replace(uuid, stack.get(uuid) + 1);
			

			float damage = (float) (1 + 0.25 * level);
			Vector v = user.getLocation().getDirection();
			Location loc = user.getEyeLocation();
			World w = user.getWorld();
			v.multiply(0.2);
			List<UUID> damaged = new ArrayList<UUID>();
			boolean knockup = stack.get(uuid) == 3;
			
			
			Sound s = (knockup) ? Sound.ITEM_TRIDENT_RIPTIDE_3 : Sound.ENTITY_PLAYER_ATTACK_CRIT;
			w.playSound(user.getLocation(), s, 1, knockup ? 0 : 1);
			
			for (int i = 0; i < 40; i++) {
				w.spawnParticle(Particle.CLOUD, loc, 0, 0, knockup ? 0.3 : 0, 0);
				loc.add(v);
				if (!loc.getBlock().getType().equals(Material.AIR)) break;
				Iterator<Entity> iter = w.getNearbyEntities(loc, 0.25, 0.25, 0.25).iterator();
				while(iter.hasNext()) {
					Entity en = iter.next();
					if ((en instanceof LivingEntity) && !damaged.contains(en.getUniqueId())
							&& !en.getUniqueId().equals(uuid) && Utils.canAttack(user, (LivingEntity) en)) {
						HCraftDamageSource.damageNormalAttack(user, (LivingEntity) en, damage);
						damaged.add(en.getUniqueId());
						if (knockup) en.setVelocity(new Vector(0, 1, 0));
					}
				}
			}

			stack.replace(uuid, (stack.get(uuid) >= 3) ? 0 : stack.get(uuid));
			Cooldown.setCooldown(user.getUniqueId(), getCooldown(), CooldownType.ACTIVE);
		}
	}
	
	//Passive
	@Override
    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		double chance = 0.1;
		if (Math.random() > chance) return;
		if (!e.getCause().equals(DamageCause.ENTITY_ATTACK) && !e.getCause().equals(DamageCause.ENTITY_SWEEP_ATTACK)) return;
		if (!this.getMaterialList().contains(user.getEquipment().getItemInMainHand().getType())) return;
    	if (target instanceof LivingEntity) {
    		double knockUp = 0.7 + 0.025 * level;
			BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
			s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
				target.setVelocity(target.getVelocity().add(new Vector(0, knockUp, 0)));
			}, 1L);
    		target.getWorld().playSound(target.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 0);
    	}
	}
}
