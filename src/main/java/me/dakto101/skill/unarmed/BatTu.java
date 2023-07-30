package me.dakto101.skill.unarmed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.dakto101.api.Cooldown;
import me.dakto101.api.Cooldown.CooldownType;
import me.dakto101.api.Skill;
import me.dakto101.api.SkillEnum;
import me.dakto101.api.SkillType;

public class BatTu extends Skill {
	
	public BatTu() {
		super(SkillEnum.BAT_TU, Arrays.asList(
				"§7§nKích hoạt:§r§7 Chặn toàn bộ sát thương trong §f(3 + 0.2 X Cấp)§7 giây. (Shift + Click phải)",
				"",
				"§7§nBị động:§r§7 ",
				"§7- Có §f10%§7 cơ hội né được đòn đánh cận chiến khi dùng tay không.",
				"§7- Tăng §6(4 + 0.2 X Cấp)§7 sát thương vật lý khi dùng tay không."
				), 10d, SkillType.UNARMED);
		setFoodRequire(5);
		setActiveCooldown(25);
		setIcon(Material.TOTEM_OF_UNDYING);
	}
	
	@Override
    public List<String> getDescription(int level, final LivingEntity user) {
		List<String> description = new ArrayList<String>(this.getDescription());
    	description.replaceAll(s -> s.replace("(3 + 0.2 X Cấp)", "" + (3 + 0.2 * level)));
    	description.replaceAll(s -> s.replace("(4 + 0.2 X Cấp)", "" + (4 + 0.2 * level)));
    	return description;
    }
	
	//Active
	@Override
	public void applyInteractBlock(final Player user, final int level, final PlayerInteractEvent e) {
		if (!user.isSneaking()) return;
		if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || 
				e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			active(user, level);
		}
	}
	
	//Active
	@Override
	public void applyInteractEntity(final Player user, final int level, final PlayerInteractEntityEvent e) {
		if (!user.isSneaking()) return;
		active(user, level);
	}
	
	//Passive1
	@Override
	public void applyDefense(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageEvent e) {
		if (user.getEquipment().getItemInMainHand().getType().equals(Material.AIR)) {
			if (e.getCause().equals(DamageCause.ENTITY_ATTACK) || e.getCause().equals(DamageCause.ENTITY_SWEEP_ATTACK)) {
				//Param
				double chance = 0.1;
				//Code
				if (Math.random() > chance) return;
				e.setCancelled(true);
				user.getWorld().playSound(user.getLocation(), Sound.ENTITY_PLAYER_ATTACK_NODAMAGE, 1, 1);
				user.getWorld().playSound(user.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1, 1);
			}
		}

	}
	
	//Passive2
	@Override
	public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (user.getEquipment().getItemInMainHand().getType().equals(Material.AIR)) {
			if (!e.getCause().equals(DamageCause.ENTITY_ATTACK)) return;
			double bonusDamage = 4 + 0.2 * level;
			e.setDamage(e.getDamage() + bonusDamage);
		}
	}
	
	private void active(final Player user, final int level) {
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

		int duration = (int) (20 * (3 + 0.2 * level));
		user.swingMainHand();
		user.getWorld().playSound(user.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1, 1);
		user.getWorld().playEffect(user.getLocation(), Effect.MOBSPAWNER_FLAMES, 20);
		user.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, duration, 100, false, false, false));

		
		Cooldown.setCooldown(user.getUniqueId(), getActiveCooldown(), CooldownType.ACTIVE_SKILL);
	}
	
	
}
