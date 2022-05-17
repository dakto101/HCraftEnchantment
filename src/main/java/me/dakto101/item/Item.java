package me.dakto101.item;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public abstract class Item {
	protected ItemType itemType;
	protected List<String> description;
	protected Material material;

	
	protected Item(final ItemType itemType, final List<String> description, final Material material) {
		this.itemType = itemType;
		this.description = description;
		this.material = material;
	}
	
    // ---- Getters/Setters ---- //

    
    /**
     * @param item to check.
     * @return true if item is parsable.
     */
    public abstract boolean isParsable(ItemStack item);
    
    /**
     * @return item.
     */
	public ItemType getItemType() {
		return itemType;
	}
	
	/**
	 * @return the description.
	 */
	public List<String> getDescription() {
		return description;
	}
	
	/**
	 * @return material of item.
	 */
	public Material getMaterial() {
		return material;
	}
	
	/**
	 * 
	 * @return item name.
	 */
	public String getName() {
		return this.getItemType().getName();
	}
    
    // --- Functional Methods --- //
    
	public abstract void parse(ItemStack item);
	
	public abstract ItemStack createItem();
	
	public abstract void click(HumanEntity p, ItemStack clickedItem);
	
	@Override
	public abstract String toString();
    
}
