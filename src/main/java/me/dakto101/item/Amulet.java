package me.dakto101.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Amulet extends Item {
	
	private ItemQuality quality;
	private static final String QUALITY = "§aPhẩm chất: ";

	public Amulet() {
		super(ItemType.AMULET, Arrays.asList(
				"§7Lá bùa giúp giữ lại trang bị nếu phù phép đặc biệt",
				"§7không thành công."
		), Material.FLOWER_BANNER_PATTERN);	
		this.quality = ItemQuality.ELITE;
	}
	
    // ---- Getters/Setters ---- //

	@Override
	public boolean isParsable(ItemStack item) {
		if (!item.getType().equals(this.material)) return false;
		ItemMeta itemMeta = item.getItemMeta();
		if (!itemMeta.getDisplayName().equals(this.getItemType().getName())) return false;
		
		List<String> itemLore = item.getItemMeta().getLore();
		boolean quality = false;
		for (String s : itemLore) {
			
			if (s.contains(QUALITY)) quality = true;
		}
		if (quality == false) return false;
		if (!itemLore.containsAll(getDescription())) return false;
		
		if (itemMeta.getEnchants().size() != 1 || !itemMeta.hasEnchant(Enchantment.DURABILITY)) return false;
		
		return true;
	}
	/**
	 * @return the quality
	 */
	public ItemQuality getQuality() {
		return quality;
	}
	
	// --- Functional Methods --- //

	@Override
	public void parse(ItemStack item) {
		if (!this.isParsable(item)) return;
	}

	@Override
	public ItemStack createItem() {
		ItemStack item = new ItemStack(this.material);
		//Meta
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(this.getItemType().getName());
		itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
		//Lore
		List<String> itemLore = new ArrayList<String>();
		itemLore.add("");
		itemLore.addAll(this.getDescription());
		itemLore.add("");
		itemLore.add(QUALITY + this.getQuality().getName());
		//
		itemMeta.setLore(itemLore);
		item.setItemMeta(itemMeta);
		return item;
	}

	@Override
	public void click(HumanEntity p, ItemStack clickedItem) {
		// TODO Auto-generated method stub
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}
