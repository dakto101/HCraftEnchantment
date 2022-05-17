package me.dakto101.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.dakto101.util.LoreReader;

public abstract class CustomEnchantment {


    /**
     * Default conflict group for enchantments. Using this group causes the
     * enchantment not to conflict with any other enchantments.
     */
    public static final String DEFAULT_GROUP = "Default";

    private String name;
    private String description;
    private double rarity;

    private boolean enabled;
    private boolean stacks;
    private int foodRequire;
    private double cooldown;
    private CustomEnchantmentType type;


    protected CustomEnchantment(final String name, final String description, final double rarity) {
        Validate.notEmpty(name, "The name must be present and not empty");
        Validate.notEmpty(description, "The description must be present and not empty");

        this.name = name.trim();
        this.description = description.trim();
        this.rarity = rarity;

        enabled = true;
        stacks = false;
        foodRequire = 0;
        cooldown = 0;
        type = CustomEnchantmentType.OTHER;
    }

    // ---- Getters/Setters ---- //

    /**
     * @return name of the enchantment that shows up in the lore
     */
    public String getName() {
        return name;
    }

    /**
     * @return details for the enchantment to show in the details book
     */
    public String getDescription() {
        return description;
    }

    /**
	 * @return the rarity
	 */
	public double getRarity() {
		return rarity;
	}

	/**
     * @return whether or not having the enchantment on multiple items stacks their effects
     */
    public boolean canStack() {
        return stacks;
    }

    /** @see CustomEnchantment#canStack() */
    public void setCanStack(final boolean stacks) {
        this.stacks = stacks;
    }

    /**
     * @return whether or not the enchantment is obtainable without commands
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * @return food require to cast the enchantment.
     */
	public int getFoodRequire() {
		return foodRequire;
	}

    /**
     * @param foodRequire food require.
     */
	public void setFoodRequire(int foodRequire) {
		this.foodRequire = foodRequire;
	}
	
    /**
     * @return cooldown.
     */
	public double getCooldown() {
		return cooldown;
	}

    /**
     * @param cooldown require.
     */
	public void setCooldown(double cooldown) {
		this.cooldown = cooldown;
	}
	
	/**
	 * 
	 * @param level
	 * @param player
	 * @return description, base on level.
	 */
	public String getDescription(int level, final LivingEntity player) {
		return this.description;
	}
	
    /**
     * @return enchant type.
     */
	public CustomEnchantmentType getType() {
		return type;
	}
	
    /**
     * @param type custom enchantment type.
     */
	public void setType(CustomEnchantmentType type) {
		this.type = type;
	}

    // --- Functional Methods --- //

    /**
     * @param item item to add to
     * @param level enchantment level
     * @return item with the enchantment
     */
    public ItemStack addToItem(final ItemStack item, final int level) {
        Validate.notNull(item, "Item cannot be null");
        Validate.isTrue(level > 0, "Level must be at least 1");

        if (item.getType() == Material.BOOK) {
            item.setType(Material.ENCHANTED_BOOK);
        }

        final ItemMeta meta = item.getItemMeta();
        final List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

        final int lvl = CustomEnchantmentAPI.getCustomEnchantments(item).getOrDefault(this, 0);
        if (lvl > 0) {
            lore.remove(LoreReader.formatEnchantment(this, lvl));
        }

        lore.add(0, LoreReader.formatEnchantment(this, level));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * @param item item to remove from
     * @return item without this enchantment
     */
    public ItemStack removeFromItem(final ItemStack item) {
        Objects.requireNonNull(item, "Item cannot be null");

        final int lvl = 1;
        if (lvl > 0) {
            final ItemMeta meta = item.getItemMeta();
            final List<String> lore = meta.getLore();
            lore.remove(LoreReader.formatEnchantment(this, lvl));
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    // ---- API for effects ---- //

    /**
     * Applies the enchantment affect when attacking someone
     *
     * @param user   the entity that has the enchantment
     * @param target the entity that was struck by the enchantment
     * @param level  the level of the used enchantment
     * @param event  the event details
     */
    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent event) { }

    /**
     * Applies the enchantment defensively (when taking damage)
     *
     * @param user   the entity hat has the enchantment
     * @param target the entity that attacked the enchantment, can be null
     * @param level  the level of the used enchantment
     * @param event  the event details (EntityDamageByEntityEvent, EntityDamageByBlockEvent, or just EntityDamageEvent)
     */
    public void applyDefense(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageEvent event) { }

    /**
     * Applies effects while breaking blocks (for tool effects)
     *
     * @param user  the player with the enchantment
     * @param block the block being broken
     * @param event the event details (either BlockBreakEvent or BlockDamageEvent)
     */
    public void applyBreak(final LivingEntity user, final Block block, final int level, final BlockEvent event) { }

    /**
     * Applies effects when the item is equipped
     *
     * @param user  the player that equipped it
     * @param level the level of enchantment
     */
    public void applyEquip(final LivingEntity user, final int level) { }

    /**
     * Applies effects when the item is unequipped
     *
     * @param user  the player that unequipped it
     * @param level the level of enchantment
     */
    public void applyUnequip(final LivingEntity user, final int level) { }

    /**
     * Applies effects when the player left or right clicks (For other kinds of enchantments like spells)
     *
     * @param user  the player with the enchantment
     * @param event the event details
     */
    public void applyInteractBlock(final Player user, final int level, final PlayerInteractEvent event) { }

    /**
     * Applies effects when the player interacts with an entity
     *
     * @param user  player with the enchantment
     * @param level enchantment level
     * @param event the event details
     */
    public void applyInteractEntity(final Player user, final int level, final PlayerInteractEntityEvent event) { }

    /**
     * Applies effects when firing a projectile
     *
     * @param user  entity firing the projectile
     * @param level enchantment level
     * @param event the event details
     */
    public void applyProjectile(final LivingEntity user, final int level, final ProjectileLaunchEvent event) { }

    /**
     * Applies effects when entity firing a projectile
     *
     * @param user  entity firing the projectile
     * @param level enchantment level
     * @param event the event details
     */
    public void applyProjectile(final LivingEntity user, final int level, final EntityShootBowEvent event) { }

    /**
     * Applies effects when entity die.
     *
     * @param user  entity die
     * @param level enchantment level
     * @param event the event details
     */
    public void applyDeath(final LivingEntity user, final int level, final EntityDeathEvent event) { }
    
    // ---- Object operations ---- //

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof CustomEnchantment && ((CustomEnchantment) other).name.equals(name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
	
}
