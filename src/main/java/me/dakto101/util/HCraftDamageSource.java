package me.dakto101.util;

import net.minecraft.world.damagesource.DamageSource;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftLivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.ThrownPotion;

public class HCraftDamageSource {

	/**
	 * Damage entity with generic damage. This will call EntityDamageByEntityEvent.
	 *
	 * @param source attacker entity that is attacking
	 * @param target targets which the attacker is trying to attack
	 * @param damage amount
	 *
	 */
	public static boolean damageGeneric(final LivingEntity source, final LivingEntity target, final float damage) {
		if (!Utils.canAttack(source, target)) return false;
		CraftLivingEntity entity = (CraftLivingEntity) target;

		DamageSource reason = entity.getHandle().damageSources().generic();
		entity.getHandle().hurt(reason, damage);

		return true;
	}

	/**
	 * Damage entity with sting damage. This will call EntityDamageByEntityEvent.
	 *
	 * @param source attacker entity that is attacking
	 * @param target targets which the attacker is trying to attack
	 * @param damage amount
	 *
	 */
	public static boolean damageSting(final LivingEntity source, final LivingEntity target, final float damage) {
		if (!Utils.canAttack(source, target)) return false;
		CraftLivingEntity entity = (CraftLivingEntity) target;

		DamageSource reason = entity.getHandle().damageSources().sting(((CraftLivingEntity) source).getHandle());
		entity.getHandle().hurt(reason, damage);

		return true;
	}

	/**
	 * Damage entity with normal attack damage. This will call EntityDamageByEntityEvent.
	 *  @param source attacker entity that is attacking
	 * @param target targets which the attacker is trying to attack
	 * @param damage amount
	 *
	 */
	public static void damageNormalAttack(final LivingEntity source, final LivingEntity target, final float damage) {
		if (!Utils.canAttack(source, target)) return;
		CraftLivingEntity entity = (CraftLivingEntity) target;

		DamageSource reason = entity.getHandle().damageSources().generic();
		if (source instanceof HumanEntity) {
			reason = entity.getHandle().damageSources().playerAttack(((CraftHumanEntity) source).getHandle());
		} else if (source instanceof LivingEntity) {
			reason = entity.getHandle().damageSources().mobAttack(((CraftLivingEntity) source).getHandle());
		}
		entity.getHandle().hurt(reason, damage);
	}
	
	
	/**
	 * Damage entity with magic damage. This will call EntityDamageByEntityEvent.
	 *
	 * @param source attacker entity that is attacking
	 * @param target targets which the attacker is trying to attack
	 * @param damage amount
	 *
	 */
	public static boolean damageIndirectMagic(final LivingEntity source, final LivingEntity target, final float damage) {
		if (!Utils.canAttack(source, target)) return false;
		CraftLivingEntity entity = (CraftLivingEntity) target;
		// Summon this to change damage type into MAGIC.
		ThrownPotion trigger = (ThrownPotion) source.getWorld().spawnEntity(source.getLocation(), EntityType.SPLASH_POTION);
		DamageSource reason = entity.getHandle().damageSources().indirectMagic(((CraftEntity) trigger).getHandle(), ((CraftEntity) source).getHandle());
		entity.getHandle().hurt(reason, damage);
		trigger.remove();
		return true;
	}
	
	/**
	 * Damage entity with thorn damage. This will call EntityDamageByEntityEvent.
	 *
	 * @param source attacker entity that is attacking
	 * @param target targets which the attacker is trying to attack
	 * @param damage amount
	 *
	 */
	public static boolean damageThorns(final LivingEntity source, final LivingEntity target, final float damage) {
		if (!Utils.canAttack(source, target)) return false;
		CraftLivingEntity entity = (CraftLivingEntity) target;

		DamageSource reason = entity.getHandle().damageSources().thorns(((CraftEntity) source).getHandle());
		entity.getHandle().hurt(reason, damage);
		return true;
	}

	/**
	 * Damage entity with explode damage. This will call EntityDamageByEntityEvent.
	 *
	 * @param source entity that is attacking
	 * @param target targets which the attacker is trying to attack
	 * @param damage amount
	 *
	 */
	public static boolean damageExplosion(final LivingEntity source, final LivingEntity target, final float damage) {
		if (!Utils.canAttack(source, target)) return false;
		CraftLivingEntity entity = (CraftLivingEntity) target;

		DamageSource reason = entity.getHandle().damageSources().explosion(((CraftEntity) entity).getHandle(), ((CraftEntity) source).getHandle());
		entity.getHandle().hurt(reason, damage);

		return true;
	}

	/**
	 * Damage entity with sonic damage. This will call EntityDamageByEntityEvent.
	 *
	 * @param source entity that is triggering sonic boom
	 * @param target targets which the attacker is trying to attack
	 * @param damage amount
	 *
	 */
	public static boolean damageSonicBoom(final LivingEntity source, final LivingEntity target, final float damage) {
		if (!Utils.canAttack(source, target)) return false;
		CraftLivingEntity entity = (CraftLivingEntity) target;

		DamageSource reason = entity.getHandle().damageSources().sonicBoom(((CraftLivingEntity) source).getHandle());
		entity.getHandle().hurt(reason, damage);

		return true;
	}

	/**
	 * Damage entity with fall damage. This will call EntityDamageByEntityEvent.
	 *
	 * @param source attacker entity that is attacking
	 * @param target targets which the attacker is trying to attack
	 * @param damage amount
	 *
	 */
	public static boolean damageFall(final LivingEntity source, final LivingEntity target, final float damage) {
		if (!Utils.canAttack(source, target)) return false;
		CraftLivingEntity entity = (CraftLivingEntity) target;

		DamageSource reason = entity.getHandle().damageSources().fall();
		entity.getHandle().hurt(reason, damage);

		return true;
	}



}
