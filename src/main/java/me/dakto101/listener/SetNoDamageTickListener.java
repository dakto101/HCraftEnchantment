package me.dakto101.listener;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class SetNoDamageTickListener implements Listener {
	
	
    /**
     * Set no damage ticks.
     */
	@EventHandler(priority = EventPriority.MONITOR)
	public static void setNoDamageTicks(final EntityDamageEvent e) {
		
		if (e.getEntity() instanceof LivingEntity) {
			LivingEntity entity = (LivingEntity) e.getEntity();
			entity.setNoDamageTicks(0);
		}
		
	}
	
    /**
     * Test listener
     */
	
    @EventHandler(priority = EventPriority.MONITOR)
    public static void onDefense(final EntityDamageEvent e) {

		if (e.getCause().equals(DamageCause.CUSTOM)) return;
		if (e.getEntity() == null) return;
		if (!(e.getEntity() instanceof LivingEntity)) return;
    	
    }
    
    /*
    @SuppressWarnings("unused")
	private static LivingEntity getDamager(final EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof LivingEntity) {
            return (LivingEntity) event.getDamager();
        }
        else if (event.getDamager() instanceof Projectile) {
            final Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof LivingEntity) {
                return (LivingEntity) projectile.getShooter();
            }
        }
        return null;
    }
    */
}
