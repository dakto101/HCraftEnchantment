package me.dakto101.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AdminListener implements Listener {

	public static final List<UUID> TOGGLE = new ArrayList<UUID>();

	@EventHandler(priority = EventPriority.MONITOR)
	public static void onAttacks(EntityDamageEvent e) {
		if (TOGGLE.size() == 0) return;

		if (e.getCause().equals(EntityDamageEvent.DamageCause.CUSTOM)) return;
		if (!(e.getEntity() instanceof LivingEntity)) return;
		if (TOGGLE.contains(e.getEntity().getUniqueId())) {
			e.getEntity().sendMessage("§aDamage type = " + e.getCause() + ", final damage = " + e.getFinalDamage() + ", total damage = " + e.getDamage());
		}
		e.getEntity().getNearbyEntities(5, 5, 5).stream().filter(entity -> TOGGLE.contains(entity.getUniqueId())).collect(Collectors.toList()).forEach(entity -> {
			entity.sendMessage("§aDamage type = " + e.getCause() + ", final damage = " + e.getFinalDamage() + ", total damage = " + e.getDamage());
		});
	}

	/**
	 *
	 * @param event event
	 * @return damager
	 */
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





}
