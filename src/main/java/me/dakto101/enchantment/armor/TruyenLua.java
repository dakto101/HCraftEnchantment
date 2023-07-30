package me.dakto101.enchantment.armor;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitScheduler;

import me.dakto101.HCraftEnchantment;
import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentType;

public class TruyenLua extends CustomEnchantment {

	public TruyenLua() {
		super("Truyền lửa", "§7Nếu bạn đang bị thiêu đốt, kẻ địch tấn công bạn sẽ "
				+ "bị hấp thụ hiệu ứng thiêu đốt thay bạn và tăng thời gian thiêu đốt "
				+ "đó thêm §f(0.5 X Cấp)§7 giây.", 30);
		setCanStack(true);
		setType(CustomEnchantmentType.DEFENSE);
	}
	
	@Override
	public String getDescription(int level, final LivingEntity user) {
		return this.getDescription().replace("(0.5 X Cấp)", "" + (0.5 * level));
	}
	
	@Override
    public void applyDefense(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageEvent e) {
		if (target == null) return;
		
		if (e.getCause().equals(DamageCause.ENTITY_ATTACK) || 
			e.getCause().equals(DamageCause.ENTITY_SWEEP_ATTACK) || 
			e.getCause().equals(DamageCause.PROJECTILE)) {

			// Tạo scheduled task để tránh deadlock
			BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
			s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
				int duration = user.getFireTicks();
				if (duration <= 0) return;
				duration += 10 * level;
				target.setFireTicks(duration);
				user.setFireTicks(0);
			}, 1L);
			
			
		}
	}
	
	@Override
    public void applyDeath(final LivingEntity user, final int level, final EntityDeathEvent event) { }

	
}
