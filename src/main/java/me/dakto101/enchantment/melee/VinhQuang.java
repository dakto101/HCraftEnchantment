package me.dakto101.enchantment.melee;

import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import me.dakto101.HCraftEnchantment;
import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentType;

public class VinhQuang extends CustomEnchantment {

	public VinhQuang() {
		super("Vinh quang", "§7Nếu tiêu diệt được mục tiêu, hồi lại §c(1 + 1 X Cấp)"
				+ "§7 máu. Nếu mục tiêu là người chơi, nhận thêm hiệu ứng §bTốc độ II"
				+ "§7, §bSức mạnh II§7 và §bHồi phục II§7 trong 4 giây.", 10);
		setCanStack(false);
		setType(CustomEnchantmentType.MELEE);
	}

	@Override
	public String getDescription(int level, final LivingEntity user) {
		return this.getDescription().replace("(1 + 1 X Cấp)", "" + (1 + 1 * level));
	}
	
	@Override
    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (e.getCause().equals(DamageCause.ENTITY_ATTACK) || 
				e.getCause().equals(DamageCause.ENTITY_SWEEP_ATTACK)) {

			
			BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
			s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
				if (target.isDead()) {
					double amount = 1 + 1 * level;
					double maxHealth = user.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
					double health = user.getHealth();
					user.setHealth((health + amount >= maxHealth) ? maxHealth : health + amount);
					if (target instanceof Player) {
						user.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 80, 1, true, true, true));
						user.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 80, 1, true, true, true));
						user.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 80, 1, true, true, true));
					}
					user.getWorld().playSound(user.getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 1);
				}
			}, 1L);
			
		}
	}
	
}
