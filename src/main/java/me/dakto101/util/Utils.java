package me.dakto101.util;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Animals;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

public class Utils {

    public static boolean isPresent(final ItemStack item) {
        return item != null;
        //return item != null && item.getType() != Material.AIR;
    }
    
    /**
     * Checks whether or not an entity can be attacked by a player
     *
     * @param attacker player trying to attack
     * @param target   target of the attack
     *
     * @return true if the target can be attacked, false otherwise
     */
    public static boolean canAttack(final LivingEntity attacker, final LivingEntity target) {
        return canAttack(attacker, target, false);
    }

    /**
     * Checks whether or not an entity can be attacked by a player
     *
     * @param attacker    player trying to attack
     * @param target      target of the attack
     * @param passiveAlly whether or not passive mobs are considered allies
     *
     * @return true if the target can be attacked, false otherwise
     */
    public static boolean canAttack(final LivingEntity attacker, final LivingEntity target, boolean passiveAlly) {
        if (attacker == target) { return false; }
        if (target instanceof Tameable) {
            Tameable entity = (Tameable) target;
            if (entity.isTamed() && entity.getOwner() instanceof OfflinePlayer) {
                final OfflinePlayer owner = (OfflinePlayer) entity.getOwner();
                if (owner.isOnline()) {
                    return canAttack(attacker, owner.getPlayer(), false);
                }
            }
        } else if (passiveAlly && target instanceof Animals) {
            return false;
        }
        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(
                attacker,
                target,
                DamageCause.CUSTOM,
                0.01);
        Bukkit.getPluginManager().callEvent(event);
        return !event.isCancelled();
    }
    
    /**
     * Retrieves all living entities the entity can attack from the list
     *
     * @param attacker entity that is attacking
     * @param targets  targets the player is trying to attack
     *
     * @return list of targets the player can attack
     */
    public static List<LivingEntity> canAttack(final LivingEntity attacker, final 
    		List<LivingEntity> targets) {
    	return targets.stream().filter(target -> canAttack(attacker, target))
    			.collect(Collectors.toList());

    }
	
}
