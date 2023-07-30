package me.dakto101.enchantment.melee;

import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.dakto101.api.Cooldown;
import me.dakto101.api.Cooldown.CooldownType;
import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentType;

public class CuongHuyet extends CustomEnchantment {

	public CuongHuyet() {
		super("Cuồng huyết", "§7Đòn đánh cận chiến hồi cho bạn §c1 + 0.5 X Cấp + 12% máu tối đa§7"
				+ "và nhận hiệu ứng §fTốc độ II§7 trong 3 giây. (20s hồi)", 220);
		setCanStack(false);
		setType(CustomEnchantmentType.MELEE);
		setCooldown(20);
	}
	
	@Override
	public String getDescription(int level, final LivingEntity user) {
		double maxHealth = user.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		return this.getDescription().replace("1 + 0.5 X Cấp + 12% máu tối đa", "" + (1 + 0.5 * level + 0.12 * maxHealth));
	}

	@Override
    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (e.getCause().equals(DamageCause.ENTITY_ATTACK) || 
				e.getCause().equals(DamageCause.ENTITY_SWEEP_ATTACK)) {
			//Cooldown
			if (Cooldown.onCooldown(user.getUniqueId(), CooldownType.MELEE_ENCHANTMENT)) return;
			
			double maxHealth = user.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
			double health = user.getHealth();
			double amount = 1 + 0.5 * level + (0.12 * maxHealth > 20 ? 20 : 0.12 * maxHealth);
			
			user.setHealth((health + amount >= maxHealth) ? maxHealth : health + amount);
			user.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 1, true, true, true));
			user.sendMessage("§aNhận §c" + amount + "§a máu và tăng tốc độ chạy.");
			user.getWorld().playSound(user.getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 1);
			//Cooldown start timing.
			Cooldown.setCooldown(user.getUniqueId(), getCooldown(), CooldownType.MELEE_ENCHANTMENT);
		}
		

	}
	
}
