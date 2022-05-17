package me.dakto101.enchantment.armor;

import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import me.dakto101.HCraftEnchantment;
import me.dakto101.api.Cooldown;
import me.dakto101.api.Cooldown.CooldownType;
import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentType;

public class BaoHiem extends CustomEnchantment {

	public BaoHiem() {
		super("Bảo hiểm", "§7Nhận lớp lá chắn hấp thụ §e(0.5 X Cấp + 10% máu tối đa) "
				+ "§7sát thương nhận vào trong 5 giây khi còn dưới §c40%§7 máu. (100s hồi)", 400);
		setCanStack(true);
		setType(CustomEnchantmentType.DEFENSE);
		setCooldown(100);
	}
	
	@Override
	public String getDescription(int level, final LivingEntity user) {
		double maxHealth = user.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		return this.getDescription().replace("0.5 X Cấp + 10% máu tối đa", "" + (0.5 * level + 0.1 * maxHealth));
	}
	
	@Override
    public void applyDefense(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageEvent e) {
		if (Cooldown.onCooldown(user.getUniqueId(), CooldownType.ARMOR)) return;
		//
		double health = user.getHealth();
		double maxHealth = user.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		if (health / maxHealth > 0.4) return;
		//
		double absorption = 0.5 * level + maxHealth * 0.1;
		int duration = 100;
		user.sendMessage("§6" + this.getName() + "§7 đã kích hoạt lá chắn hấp thụ "
				+ "§6" + absorption + "§7 sát thương nhận vào.");
		user.setAbsorptionAmount(absorption);
		user.getWorld().playEffect(user.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
		user.getWorld().playSound(user.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1, 1);
		user.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, duration, -1));
		//Remove absorption
		BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
		s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
			user.setAbsorptionAmount(0);
		}, duration);
		
		Cooldown.setCooldown(user.getUniqueId(), getCooldown(), CooldownType.ARMOR);
	}

	
}
