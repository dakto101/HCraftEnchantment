package me.dakto101.util;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import net.minecraft.world.damagesource.DamageSource;

public class HCraftDamageSource {
	
	/** 
	 * Damage entity with specific damage. This will call EntityDamageByEntityEvent.
	 * 
	 * @param damager
	 * @param target
	 * @param dmgSrc
	 * @param damage
	 * @return
	 */
	@Deprecated
	public static boolean damage(final LivingEntity damager, final LivingEntity target, final DamageCause dmgSrc, final double damage) {
		if (!Utils.canAttack(damager, target)) return false;
		EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager, target, dmgSrc, damage);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		double finalDamage = event.getFinalDamage();
		target.damage(finalDamage, damager);
		return true;
	}

	/**
	 * Damage entity with specific damage.
	 *
	 * @param user attacker entity that is attacking
	 * @param target target the player is trying to attack
	 * @param src type of damage
	 * @param damage damage
	 *
	 */

	public static boolean damage(final LivingEntity user, final LivingEntity target, final DamageSource src, final float damage) {
			if (!Utils.canAttack(user, target)) return false;
			CraftLivingEntity entity = (CraftLivingEntity) target;
			entity.getHandle().hurt(src, damage);
			return true;
	}

	/**
	 * Damage entity with specific damage. This only calls EntityDamageEvent.
	 * 
	 * @param user
	 * @param target
	 * @param src
	 * @param damage
	 * @return
	 */
	public static boolean damage(final LivingEntity user, final LivingEntity target, final DamageSourceEnum src, final float damage) {
		return damage(user, target, src.getDamageSource(), damage);
	}
	
	/**
	 * Damage entity with sting damage. This will call EntityDamageByEntityEvent.
	 *
	 * @param user attacker entity that is attacking
	 * @param target targets of attacker
	 * @param damage amount
	 *
	 */
	public static boolean damageSting(final LivingEntity user, final LivingEntity target, final float damage) {
			if (!Utils.canAttack(user, target)) return false;
			CraftLivingEntity entity = (CraftLivingEntity) target;
			//entity.getHandle().damageEntity(DamageSource.b(((CraftLivingEntity) user).getHandle()), damage);

            entity.getHandle().hurt(DamageSource.sting(((CraftLivingEntity) user).getHandle()), damage);
			return true;
	}
	
	/**
	 * Damage entity with normal attack damage. This will call EntityDamageByEntityEvent.
	 *  @param user attacker entity that is attacking
	 * @param target targets of attacker
	 * @param damage amount
	 *
	 */
	public static void damageNormalAttack(final LivingEntity user, final LivingEntity target, final float damage) {
			if (!Utils.canAttack(user, target)) return;
			CraftLivingEntity entity = (CraftLivingEntity) target;
			//entity.getHandle().a(DamageSource.b(((CraftLivingEntity) user).getHandle()), damage);
			entity.getHandle().hurt(DamageSource.mobAttack(((CraftLivingEntity) user).getHandle()), damage);
	}
	
	
	/**
	 * Damage entity with magic damage. This will call EntityDamageByEntityEvent.
	 *
	 * @param user attacker entity that is attacking
	 * @param target targets the player is trying to attack
	 * @param damage amount
	 *
	 */
	public static boolean damageMagic(final LivingEntity user, final LivingEntity target, final float damage) {
			if (!Utils.canAttack(user, target)) return false;
			CraftLivingEntity entity = (CraftLivingEntity) target;
			entity.getHandle().hurt(DamageSource.indirectMagic(((CraftEntity) user).getHandle(), null), damage);
			return true;
	}
	
	/**
	 * Damage entity with thorn damage. This will call EntityDamageByEntityEvent.
	 *
	 * @param user attacker entity that is attacking
	 * @param target targets the player is trying to attack
	 * @param damage amount
	 *
	 */
	public static boolean damageThorn(final LivingEntity user, final LivingEntity target, final float damage) {
			if (!Utils.canAttack(user, target)) return false;
			CraftLivingEntity entity = (CraftLivingEntity) target;
			entity.getHandle().hurt(DamageSource.thorns(((CraftEntity) user).getHandle()), damage);
			return true;
	}
	
	/**
	 * Damage entity with explode damage. This will call EntityDamageByEntityEvent.
	 *
	 * @param user entity that is attacking
	 * @param target targets the player is trying to attack
	 * @param damage amount
	 *
	 */
	public static boolean damageExplode(final LivingEntity user, final LivingEntity target, final float damage) {
			if (!Utils.canAttack(user, target)) return false;
			CraftLivingEntity entity = (CraftLivingEntity) target;
			entity.getHandle().hurt(DamageSource.explosion(((CraftLivingEntity) user).getHandle()), damage);
			return true;
	}

	
}
