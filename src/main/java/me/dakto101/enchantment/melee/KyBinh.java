package me.dakto101.enchantment.melee;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import me.dakto101.HCraftEnchantment;
import me.dakto101.api.Cooldown;
import me.dakto101.api.Cooldown.CooldownType;
import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentType;

public class KyBinh extends CustomEnchantment {

	public KyBinh() {
		super("Kỵ binh", "§7Khi cưỡi ngựa, đòn đánh thường đầu tiên sẽ gây hất tung, "
				+ "gây thêm §6(2.5 + 2.5 X Cấp)§7 sát thương vật lý và kẻ địch không "
				+ "thể tấn công trong 3 giây. Lượng sát thương cộng thêm giảm một nửa "
				+ "khi đang hồi. (40s hồi)", 190);
		setCanStack(false);
		setType(CustomEnchantmentType.MELEE);
		setCooldown(40);
	}
	
	@Override
	public String getDescription(int level, final LivingEntity user) {
		return this.getDescription().replace("(2.5 + 2.5 X Cấp)", "" + (2.5 + 2.5 * level));
	}

	@Override
    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (!user.getVehicle().getType().equals(EntityType.HORSE)) return;
		if (e.getCause().equals(DamageCause.ENTITY_ATTACK) || 
				e.getCause().equals(DamageCause.ENTITY_SWEEP_ATTACK)) {
			
				double bonusDamage = 2.5 + 2.5 * level;
				//Cooldown
				if (Cooldown.onCooldown(user.getUniqueId(), CooldownType.MELEE_ENCHANTMENT)) {
					bonusDamage *= 0.5;
					e.setDamage(e.getDamage() + bonusDamage);
					return;
				}
				
				e.setDamage(e.getDamage() + bonusDamage);
				target.getWorld().playSound(user.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1, 1);
				BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
				s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
					target.setVelocity(new Vector(0, 1.3, 0));
					target.getWorld().spawnParticle(Particle.CLOUD, target.getLocation(), 100);
				}, 1L);
				target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 100, true, true, true));
				user.sendMessage("§6" + this.getName() + "§7 gây thêm §6" + bonusDamage + "§7 sát thương vật lý cho mục tiêu.");
				//Cooldown start timing.
				Cooldown.setCooldown(user.getUniqueId(), getCooldown(), CooldownType.MELEE_ENCHANTMENT);

		}
		

	}
	
}
