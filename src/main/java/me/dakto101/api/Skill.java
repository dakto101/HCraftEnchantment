package me.dakto101.api;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class Skill {

    private SkillEnum skill;
    private List<String> description;
    private double rarity;
    private SkillType type;

    private boolean enabled;
    private int foodRequire;
    private double cooldown;
    private List<Material> materialList;
    private Material icon;

    /**
     * 
     * @param skill skill enum
     * @param description
     * @param rarity
     * @param type
     */
    protected Skill(final SkillEnum skill, final List<String> description, final double rarity, final SkillType type) {
        Validate.notEmpty("The name must be present and not empty");
        Validate.notEmpty(description, "The description must be present and not empty");

        this.skill = skill;
        this.description = description;
        this.rarity = rarity;
        this.type = type;

        enabled = true;
        foodRequire = 0;
        cooldown = 0;
        materialList = getDefaultMaterialList();
        icon = getDefaultIcon();
    }

    // ---- Getters/Setters ---- //

    /**
	 * @return the id
	 */
	public int getId() {
		return this.skill.getId();
	}

	/**
     * @return name of the enchantment that shows up in the lore
     */
    public String getName() {
        return this.skill.getName();
    }
    
	/**
     * @return skill enum
     */
    public SkillEnum getSkillEnum() {
        return this.skill;
    }

    /**
     * @return details for the enchantment to show in the details book
     */
    public List<String> getDescription() {
        return description;
    }
    
    /**
     * @param level
     * @param user
     * 
     * @return details for the enchantment to show in the details book
     */
    public List<String> getDescription(int level, final LivingEntity user) {
        return description;
    }

    /**
	 * @return the rarity
	 */
	public double getRarity() {
		return rarity;
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
	 * @return the type
	 */
	public SkillType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(SkillType type) {
		this.type = type;
	}
	
	/**
	 * @return the icon
	 */
	public Material getIcon() {
		return icon;
	}

	/**
	 * @param icon the icon to set
	 */
	public void setIcon(Material icon) {
		this.icon = icon;
	}

	/**
	 * @return the materialList
	 */
	public List<Material> getMaterialList() {
		return materialList;
	}

	/**
	 * @param materialList the materialList to set
	 */
	public void setMaterialList(List<Material> materialList) {
		this.materialList = materialList;
	}
	
	private List<Material> getDefaultMaterialList() {
		switch (this.getType()) {
		case ARCHERY:
			return Arrays.asList(Material.BOW, Material.CROSSBOW);
		case MAGIC:
			return Arrays.asList(Material.ENCHANTED_BOOK, Material.KNOWLEDGE_BOOK, Material.BOOK);
		case OTHER:
			return Arrays.asList();
		case SWORDSMANSHIP:
			return Arrays.asList(Material.NETHERITE_AXE, Material.DIAMOND_AXE, Material.IRON_AXE, 
					Material.GOLDEN_AXE, Material.STONE_AXE, Material.WOODEN_AXE, 
					Material.NETHERITE_SWORD, Material.DIAMOND_SWORD, Material.IRON_SWORD, 
					Material.GOLDEN_SWORD, Material.STONE_SWORD, Material.WOODEN_SWORD);
		case UNARMED:
			return Arrays.asList(Material.AIR);
		default:
			return null;
		}
	}
	
	private Material getDefaultIcon() {
		switch (this.getType()) {
		case ARCHERY:
			return Material.BOW;
		case MAGIC:
			return Material.SPLASH_POTION;
		case OTHER:
			return Material.STICK;
		case SWORDSMANSHIP:
			return Material.WOODEN_SWORD;
		case UNARMED:
			return Material.RABBIT_FOOT;
		default:
			return Material.PAPER;
		}
	}
	
    // --- Functional Methods --- //



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
     * Applies the enchantment affect when user's vehicle taking damage
     *
     * @param userVehicle the user's vehicle
     * @param target the entity that was struck by the enchantment
     * @param level  the level of the used enchantment
     * @param event  the event details
     */
    public void applyOnVehicleHit(final LivingEntity user, final LivingEntity userVehicle, final LivingEntity target, final int level, final EntityDamageEvent event) { }
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
    
    /**
     * Applies effects when entity die.
     *
     * @param user  entity regain health
     * @param level enchantment level
     * @param event the event details
     */
    public void applyRegainHealth(final LivingEntity user, final int level, final EntityRegainHealthEvent event) { }
    
    // ---- Object operations ---- //

    @Override
    public String toString() {
        return this.skill.getName();
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof Skill && ((Skill) other).skill.equals(skill);
    }

    @Override
    public int hashCode() {
        return skill.hashCode();
    }
	
}
