package me.dakto101.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentAPI;

public class AdvancedEnchantedBook extends Item {
	

	//chance in percent (0-100)
	private double chance;
	private Map<CustomEnchantment, Integer> enchantments;
	private ItemQuality quality;
	
	private static final String CHANCE = "§aXác suất (%): §7";
	private static final String QUALITY = "§aPhẩm chất: ";
	
	public AdvancedEnchantedBook() {
		super(ItemType.ADVANCED_ENCHANTED_BOOK, Arrays.asList(
			"§7Sách chứa các phù phép cao cấp. Không thể ép",
			"§7vào các vật phẩm đã được phù phép cao cấp. Nếu ",
			"§7thất bại sẽ vật phẩm phù phép sẽ mất."
		), Material.BOOK);	
		this.enchantments = new HashMap<CustomEnchantment, Integer>();
		this.quality = ItemQuality.COMMON;
		this.chance = 0d;
	}
	
    // ---- Getters/Setters ---- //

	
	/**
	 * @return a sample item.
	 */
	@Override
	public boolean isParsable(ItemStack item) {
		if (!item.getType().equals(this.material)) return false;
		if (!item.getItemMeta().getDisplayName().equals(this.getItemType().getName())) return false;
		
		List<String> itemLore = item.getItemMeta().getLore();
		boolean chance = false;
		boolean quality = false;
		for (String s : itemLore) {
			
			if (s.contains(CHANCE)) chance = true;
			if (s.contains(QUALITY)) quality = true;
		}
		if (chance == false || quality == false) return false;
		if (!itemLore.containsAll(getDescription())) return false;
		
		return true;
	}
	
	/**
	 * @return the chance
	 */
	public double getChance() {
		return chance;
	}

	/**
	 * @param chance the chance to set
	 */
	public void setChance(double chance) {
		this.chance = Math.round(chance*100.0)*0.01;
		
	}

	/**
	 * @return the enchantments
	 */
	public Map<CustomEnchantment, Integer> getEnchantments() {
		return enchantments;
	}

	/**
	 * @param enchantments the enchantments to set
	 */
	public void setEnchantments(Map<CustomEnchantment, Integer> enchantments) {
		this.enchantments = enchantments;
	}

	/**
	 * @return the quality
	 */
	public ItemQuality getQuality() {
		return quality;
	}

	/**
	 * @param quality the quality to set
	 */
	public void setQuality(ItemQuality quality) {
		this.quality = quality;
	}

    // --- Functional Methods --- //
	
	/**
	 * @param must be advance enchanted book.
	 */
	@Override
	public void parse(final ItemStack item) {
		if (!this.isParsable(item)) return;
		for (String s : item.getItemMeta().getLore()) {
			if (s.contains(CHANCE)) {
				s = s.replace(CHANCE, "");
				this.setChance(Double.parseDouble(s));
			}
			if (s.contains(QUALITY)) {
				s = s.replace(QUALITY, "");
				ItemQuality q = null;
				for (ItemQuality quality : ItemQuality.values()) {
					if (quality.getName().equals(s)) {
						q = quality;
						break;
					}
				}
				this.setQuality(q);
			}
		}
		this.setEnchantments(CustomEnchantmentAPI.getCustomEnchantments(item));
	}

	/**
	 * @return a sample item.
	 */
	public static ItemStack createSampleItem() {
		ItemStack sample = new ItemStack(Material.BOOK);
		ItemMeta sampleMeta = sample.getItemMeta();
		sampleMeta.setDisplayName("" + ItemType.ADVANCED_ENCHANTED_BOOK.getName());
		sampleMeta.setLore(Arrays.asList(
			"§7Hất tung I",
			"",
			"§7Sách chứa các phù phép cao cấp. Không thể ép",
			"§7vào các vật phẩm đã được phù phép cao cấp. Nếu ",
			"§7thất bại sẽ vật phẩm phù phép sẽ mất.",
			"",
			QUALITY + ItemQuality.MYSTERY.getName(),
			CHANCE + Math.round((Math.random()*10000))*0.01
		));
		sample.setItemMeta(sampleMeta);
		
		return sample;
	}
	
	/**
	 * @return an advance enchanted book
	 */
	@Override
	public ItemStack createItem() {
		ItemStack item = new ItemStack(this.material);
		//Meta
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(this.getItemType().getName());
		//Lore
		List<String> itemLore = new ArrayList<String>();
		itemLore.add("");
		itemLore.addAll(this.getDescription());
		itemLore.add("");
		itemLore.add(QUALITY + this.getQuality().getName());
		itemLore.add(CHANCE + this.getChance());
		//
		itemMeta.setLore(itemLore);
		item.setItemMeta(itemMeta);
		this.getEnchantments().forEach((k, v) -> k.addToItem(item, v));
		item.setType(Material.BOOK);
		return item;
	}
	
	@Override
	public void click(HumanEntity p, ItemStack clickedItem) {
	}
	/**
	 *
	 * @return ItemStack → toString.
	 */
	@Override
	public String toString() {
		return createItem().toString();
	}






    
}
