package me.dakto101.listener;

import java.util.Arrays;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.PlayerInventory;

import me.dakto101.api.CustomEnchantmentAPI;

public class CustomEnchantmentListener implements Listener {
	
	private static int count = 1;

    /**
     * Applies the enchantment affect when attacking someone
     */
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public static void onHit(final EntityDamageByEntityEvent e) {
		//me.dakto101.util: Check if entity can hit other entity or not.
		
		if (e.getCause().equals(DamageCause.CUSTOM)) return;
		if (e.getEntity() == null) return;
		if (!(e.getEntity() instanceof LivingEntity)) return;
		
		
		LivingEntity p = getDamager(e);
		if (p != null) {
			CustomEnchantmentAPI.getCustomEnchantments(p.getEquipment().getItemInMainHand())
				.forEach((k, v) -> {
					k.applyOnHit(p, (LivingEntity) e.getEntity(), v, e);
				});
		}
		
	}
	
    /**
     * Applies the enchantment defensively (when taking damage)
     */
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public static void onDefense(final EntityDamageEvent e) { 
		if (e.getCause().equals(DamageCause.CUSTOM)) return;
		if (!(e.getEntity() instanceof LivingEntity)) return;
		
		if (e instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e;
			LivingEntity p = (LivingEntity) ee.getEntity();
			CustomEnchantmentAPI.getCustomEnchantments(Arrays.asList(p.getEquipment().getArmorContents()))
				.forEach((k, v) -> k.applyDefense(p, getDamager(ee), v, ee));
			return;
		} else {
			LivingEntity p = (LivingEntity) e.getEntity();
			CustomEnchantmentAPI.getCustomEnchantments(Arrays.asList(p.getEquipment().getArmorContents()))
			.forEach((k, v) -> k.applyDefense(p, null, v, e));
		}

    }

    /**
     * Applies effects while breaking blocks (for tool effects)
     */
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public static void onBreak(final BlockBreakEvent e) {
    	if (e.isCancelled()) return;
    	Player p = e.getPlayer();
    	PlayerInventory inv = p.getInventory();
    	CustomEnchantmentAPI.getCustomEnchantments(inv.getItemInMainHand())
    		.forEach((k, v) -> k.applyBreak(p, e.getBlock(), v, e));
    }
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public static void onBreak(final BlockDamageEvent e) {
    	if (e.isCancelled()) return;
    	Player p = e.getPlayer();
    	CustomEnchantmentAPI.getCustomEnchantments(e.getItemInHand())
    		.forEach((k, v) -> k.applyBreak(p, e.getBlock(), v, e));
    }

    /**
     * Applies effects when the player left or right clicks (For other 
     * kinds of enchantments like spells)
     */
    @EventHandler(priority = EventPriority.HIGH)
    public static void onInteractBlock(final PlayerInteractEvent e) {
    	Player p = e.getPlayer();
    	CustomEnchantmentAPI.getCustomEnchantments(p.getInventory().getItemInMainHand())
    		.forEach((k, v) -> k.applyInteractBlock(p, v, e));
    }

    /**
     * Applies effects when the player interacts with an entity
     */
    @EventHandler(priority = EventPriority.HIGH)
    public static void onInteractEntity(final PlayerInteractEntityEvent e) {
    	if (cast()) return;
    	Player p = e.getPlayer();
    	CustomEnchantmentAPI.getCustomEnchantments(p.getInventory().getItemInMainHand())
    		.forEach((k, v) -> k.applyInteractEntity(p, v, e));
    }

    /**
     * Applies effects when firing a projectile
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public static void onShoot(final ProjectileLaunchEvent e) {
    	if (e.getEntity() == null) return;
    	if (e.getEntity().getShooter() == null) return;
		if (e.getEntity().getShooter() instanceof LivingEntity) {
			LivingEntity entity = (LivingEntity) e.getEntity().getShooter();
			CustomEnchantmentAPI.getCustomEnchantments(entity.getEquipment().getItemInMainHand())
				.forEach((k, v) -> k.applyProjectile(entity, v, e));
		}
    	
    }
    
    /**
     * Applies effects when entity firing a projectile
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public static void onShoot(final EntityShootBowEvent e) {
    	
    	if ((e.getEntity() == null) || !(e.getEntity() instanceof LivingEntity)) return;
    	if (e.getProjectile() == null) return;
    	if (e.getBow() == null) return;
		if (e.getEntity() instanceof LivingEntity) {
			LivingEntity entity = (LivingEntity) e.getEntity();
			CustomEnchantmentAPI.getCustomEnchantments(e.getBow())
				.forEach((k, v) -> k.applyProjectile(entity, v, e));
		}
    	
    }
    
    /**
     * Applies effects when entity die
     */
    @EventHandler(priority = EventPriority.HIGH)
    public static void onDeath(final EntityDeathEvent e) {
    	
    	if ((e.getEntity() == null) || !(e.getEntity() instanceof LivingEntity)) return;
		if (e.getEntity() instanceof LivingEntity) {
			LivingEntity entity = e.getEntity();
			CustomEnchantmentAPI.getCustomEnchantments(Arrays.asList(entity.getEquipment().getArmorContents()))
			.forEach((k, v) -> k.applyDeath(entity, v, e));
		return;
		}
    	
    }
    
    @EventHandler
    public static void test(final PlayerInteractEvent e) {

    }
    
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
    
    private static boolean cast() {
    	count++;
    	if (count > 10000) count = 0;
    	return count % 2 == 0;
    }

	
}
