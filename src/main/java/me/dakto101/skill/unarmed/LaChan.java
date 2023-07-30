package me.dakto101.skill.unarmed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
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
import org.bukkit.scheduler.BukkitScheduler;

import me.dakto101.HCraftEnchantment;
import me.dakto101.api.Cooldown;
import me.dakto101.api.Cooldown.CooldownType;
import me.dakto101.api.Skill;
import me.dakto101.api.SkillEnum;
import me.dakto101.api.SkillType;

public class LaChan extends Skill {

	public LaChan() {
		super(SkillEnum.LA_CHAN, Arrays.asList(
				"§7§nKích hoạt:§r§7 Nhận lớp lá chắn hấp thụ §e(2 X Cấp + 15% máu tối đa)§7 sát thương ",
				"§7và hiệu ứng Hồi Phục I trong 5 giây. (Shift + Click phải)",
				"",
				"§7§nBị động:§r§7 ",
				"§r§7- Khi bị đánh có §f8%§7 nhận được lá chắn hấp thụ §e(4 + 0.2 X Cấp)",
				"§7sát thương.",
				"§7- Tăng §6(5 + 0.2 X Cấp)§7 sát thương vật lý khi dùng tay không."
				), 10d, SkillType.UNARMED);
		setFoodRequire(5);
		setActiveCooldown(10);
		setPassiveCooldown(5);
		setIcon(Material.MAGMA_CREAM);
	}
	
	@Override
    public List<String> getDescription(int level, final LivingEntity user) {
		double maxHealth = user.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		List<String> description = new ArrayList<String>(this.getDescription());
    	description.replaceAll(s -> s.replace("(2 X Cấp + 15% máu tối đa)", "" + (2 * level + 0.15 * maxHealth)));
    	description.replaceAll(s -> s.replace("(4 + 0.2 X Cấp)", "" + (4 + 0.2 * level)));
    	description.replaceAll(s -> s.replace("(5 + 0.2 X Cấp)", "" + (5 + 0.2 * level)));
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
    public void applyDefense(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageEvent event) {
		if (Cooldown.onCooldown(user.getUniqueId(), CooldownType.PASSIVE_SKILL)) return;

		double chance = 0.08;
		if (Math.random() > chance) return;
    	if (target instanceof LivingEntity) {
    		double absorb = 4 + 0.2 * level;
    		user.setAbsorptionAmount(absorb);
			user.getWorld().playSound(user.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 2, 2);

			Cooldown.setCooldown(user.getUniqueId(), getPassiveCooldown(), CooldownType.PASSIVE_SKILL);
		}
    }
	
	//Passive2
	@Override
	public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (user.getEquipment().getItemInMainHand().getType().equals(Material.AIR)) {
			if (!e.getCause().equals(DamageCause.ENTITY_ATTACK)) return;
			double bonusDamage = 5 + 0.2 * level;
			e.setDamage(e.getDamage() + bonusDamage);
		}
	}
	
	private void active(final Player user, final int level) {
		if (Cooldown.onCooldown(user.getUniqueId(), CooldownType.ACTIVE_SKILL)) {
			Cooldown.sendMessage(user, this.getName(), CooldownType.ACTIVE_SKILL);
			return;
		}
		//
		if (user.getFoodLevel() < getFoodRequire()) {
			user.sendMessage("§cKhông đủ điểm thức ăn!");
			return;
		} else {
			user.setFoodLevel(user.getFoodLevel() - getFoodRequire());
		}
		
		double maxHealth = user.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		double absorption = user.getAbsorptionAmount() + 2 * level + maxHealth * 0.15;
		int duration = 100;

		user.swingMainHand();
		user.sendMessage("§6" + this.getName() + "§7 đã kích hoạt lá chắn hấp thụ "
				+ "§e" + absorption + "§7 sát thương nhận vào.");
		user.setAbsorptionAmount(absorption);
		user.getWorld().playEffect(user.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
		user.getWorld().playSound(user.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 2, 2);
		user.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, duration, -1));
		user.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, 0));
		//Remove absorption
		BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
		s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
			user.setAbsorptionAmount(0);
		}, duration);
		
		
		Cooldown.setCooldown(user.getUniqueId(), getActiveCooldown(), CooldownType.ACTIVE_SKILL);
	}
	
}
