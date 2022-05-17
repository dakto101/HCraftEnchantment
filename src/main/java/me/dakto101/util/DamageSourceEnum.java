package me.dakto101.util;

import org.bukkit.entity.Entity;

import net.minecraft.world.damagesource.DamageSource;

public enum DamageSourceEnum {
	
	IN_FIRE(DamageSource.IN_FIRE),
	@Deprecated
	LIGHTNING_BOLT(DamageSource.LIGHTNING_BOLT),
	ON_FIRE(DamageSource.ON_FIRE),
	LAVA(DamageSource.LAVA),
	HOT_FLOOR(DamageSource.HOT_FLOOR),
	IN_WALL(DamageSource.IN_WALL),
	CRAMMING(DamageSource.CRAMMING),
	DROWN(DamageSource.DROWN),
	STARVE(DamageSource.STARVE),
	CACTUS(DamageSource.CACTUS),
	FALL(DamageSource.FALL),
	FLY_INTO_WALL(DamageSource.FLY_INTO_WALL),
	OUT_OF_WORLD(DamageSource.OUT_OF_WORLD),
	GENERIC(DamageSource.GENERIC),
	MAGIC(DamageSource.MAGIC),
	WITHER(DamageSource.WITHER),
	ANVIL(DamageSource.ANVIL),
	FALLING_BLOCK(DamageSource.FALLING_BLOCK),
	DRAGON_BREATH(DamageSource.DRAGON_BREATH),
	DRY_OUT(DamageSource.DRY_OUT),
	SWEET_BERRY_BUSH(DamageSource.SWEET_BERRY_BUSH),
	FREEZE(DamageSource.FREEZE),
	FALLING_STALACTITE(DamageSource.FALLING_STALACTITE),
	STALAGMITE(DamageSource.STALAGMITE);
	
	
	private DamageSource ds;
	
    /**
     * An enum set damage source.
     */
	DamageSourceEnum(DamageSource damageSource) {
		this.ds = damageSource;
	}
	
    /**
     * An enum set damage source.
     */
	DamageSourceEnum(DamageSource damageSource, Entity damager) {
		this.ds = damageSource;
	}
	
	public DamageSource getDamageSource() {
		return this.ds;
	}

}
