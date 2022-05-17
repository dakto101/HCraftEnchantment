package me.dakto101.enchantment.melee;

import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.dakto101.api.Cooldown;
import me.dakto101.api.Cooldown.CooldownType;
import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentType;

public class PhuDau extends CustomEnchantment {

	public PhuDau() {
		super("Phủ đầu", "§7Đòn đánh cận chiến gây thêm sát thương vật lý bằng "
				+ "§6(2 + Cấp X 2 + bonus)§7."
				+ "Bonus = 16% máu hiện tại mục tiêu (tối đa: 32). (30s hồi)", 200);
		setCanStack(false);
		setType(CustomEnchantmentType.MELEE);
		setCooldown(30);
	}
	
	@Override
	public String getDescription(int level, final LivingEntity user) {
		return this.getDescription().replace("(2 + Cấp X 2)", "" + (2 + level * 2));
	}

	@Override
    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (e.getCause().equals(DamageCause.ENTITY_ATTACK) || 
				e.getCause().equals(DamageCause.ENTITY_SWEEP_ATTACK)) {
			//Cooldown
			if (Cooldown.onCooldown(user.getUniqueId(), CooldownType.PASSIVE)) return;
			
			//Max target health = 200.
			double bonusDame = 2 + level * 2 + (0.16 * target.getHealth() > 32 ? 32 : 0.16 * target.getHealth());
			e.setDamage(e.getDamage() + bonusDame);
			user.getWorld().playSound(user.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1, 1);
			user.sendMessage("§6" + this.getName() + "§7 gây thêm §6" + bonusDame + "§7 sát thương vật lý cho mục tiêu.");
			//Cooldown start timing.
			Cooldown.setCooldown(user.getUniqueId(), getCooldown(), CooldownType.PASSIVE);
		}
		

	}
	
}
