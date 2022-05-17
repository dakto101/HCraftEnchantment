package me.dakto101.skill.magic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.dakto101.api.Cooldown;
import me.dakto101.api.Cooldown.CooldownType;
import me.dakto101.api.Skill;
import me.dakto101.api.SkillEnum;
import me.dakto101.api.SkillType;

@SuppressWarnings("deprecation")
public class TriLieu extends Skill {

	public TriLieu() {
		super(SkillEnum.TRI_LIEU, Arrays.asList(
				"§7§nKích hoạt:§r§7 Hồi §c(2 + 2 X Cấp)§7 cho mục tiêu, đồng thời cho mục tiêu hiệu ứng ",
				"§7Sức mạnh I trong 5 giây. Có thể tự dùng lên bản thân. (Shift + Click phải).",
				"",
				"§7§nBị động:",
				"§7- Tăng §c(5 + 5 X Cấp)%§7 lượng máu hồi nhận được.",
				"§7- Giảm §916%§7 sát thương phép nhận vào."
				), 10, SkillType.MAGIC);
		setFoodRequire(5);
		setCooldown(12);
		setIcon(Material.APPLE);
	}
	
	@Override
    public List<String> getDescription(int level, final LivingEntity user) {
		List<String> description = new ArrayList<String>(this.getDescription());
    	description.replaceAll(s -> s.replace("(2 + 2 X Cấp)", "" + (2 + 2 * level)));
    	description.replaceAll(s -> s.replace("(5 + 5 X Cấp)", "" + (5 + 5 * level)));
    	return description;
    }
	
	//Active
	@Override
	public void applyInteractEntity(final Player user, final int level, final PlayerInteractEntityEvent e) {
		if (!user.isSneaking()) return;
		Entity entity = e.getRightClicked();
		if (!(entity instanceof LivingEntity)) return;
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
		heal(user, (LivingEntity) entity, level);
		
		Cooldown.setCooldown(user.getUniqueId(), getCooldown(), CooldownType.ACTIVE);
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
			//Code
			heal(user, user, level);

			Cooldown.setCooldown(user.getUniqueId(), getCooldown(), CooldownType.ACTIVE);
		}
		
	}
	
	//Active
	private void heal(final LivingEntity user, final LivingEntity target, int level) {
		double heal = 2 + 2 * level;
		double maxHealth = target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		double health = target.getHealth();
		
		target.setHealth((health + heal > maxHealth) ? maxHealth : health + heal);
		target.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0, true, true, true));
		if (!target.equals(user)) target.sendMessage("§7Người chơi §6" + user.getName() + "§7 đã cho bạn hiệu ứng "
				+ "Sức mạnh I và hồi §c" + heal + "§7 máu cho bạn.");
		user.sendMessage("§7Hồi §c" + heal + "§7 máu cho §6" + target.getName() + "§7 và "
				+ "hiệu ứng Sức mạnh I.");
		target.getWorld().spawnParticle(Particle.HEART, target.getLocation(), 20, 0.5, 0.5, 0.5);
		target.getWorld().playSound(target.getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 1);
		target.getWorld().playSound(target.getLocation(), Sound.ENTITY_WITCH_DRINK, 1, 1);
	}
	
	//Passive1
	@Override
	public void applyRegainHealth(final LivingEntity user, final int level, final EntityRegainHealthEvent e) {
		if (e.getAmount() <= 0) return;
		double bonusHealth = e.getAmount() * (0.05 + 0.05 * level);
		e.setAmount(e.getAmount() + bonusHealth);
	}
	
	//Passive2
	@Override
    public void applyDefense(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageEvent e) {
    	if (e.getCause().equals(DamageCause.MAGIC)) {
			double reduction = e.getDamage(DamageModifier.BASE) * 0.16;
    		e.setDamage(e.getDamage() - reduction);
    	}
    }
	
}
