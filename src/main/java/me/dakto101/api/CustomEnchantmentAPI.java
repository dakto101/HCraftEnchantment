package me.dakto101.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.dakto101.util.LoreReader;
import me.dakto101.util.Utils;

public class CustomEnchantmentAPI {
	
	public static final Map<String, CustomEnchantment> CUSTOM_ENCHANTMENT = new HashMap<String, CustomEnchantment>();
	
	

	public static void registerEnchantments(CustomEnchantment... customEnchantments) {
		for (CustomEnchantment ce : customEnchantments) {
			CustomEnchantmentAPI.CUSTOM_ENCHANTMENT.put(ce.getName(), ce);
		}
	}

    /** Unregister all enchantments
     *
     */
    public static void unregisterEnchantments() {
        CustomEnchantmentAPI.CUSTOM_ENCHANTMENT.clear();
    }
	
    /**
     * @param name enchantment name
     * @return true if the enchantment is registered successfully, false otherwise
     */
    public static boolean isRegistered(final String name) {
        return name != null && CustomEnchantmentAPI.CUSTOM_ENCHANTMENT.containsKey(name);
    }

    /**
     * @param name name of the enchantment (not case-sensitive)
     * @return enchantment with the provided name
     */
    public static CustomEnchantment getEnchantment(final String name) {
        return name == null ? null : CustomEnchantmentAPI.CUSTOM_ENCHANTMENT.get(name);
    }
	
    /**
     * @param item item to grab the enchantments from
     * @return     list of custom enchantments (does not include vanilla enchantments)
     */
    public static Map<CustomEnchantment, Integer> getCustomEnchantments(final ItemStack item) {

        final HashMap<CustomEnchantment, Integer> list = new HashMap<CustomEnchantment, Integer>();
        if (!Utils.isPresent(item)) return list;

        final ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore()) return list;

        final List<String> lore = meta.getLore();
        for (final String line : lore) {
            final String name = LoreReader.parseEnchantmentName(line);
            if (CustomEnchantmentAPI.isRegistered(name)) {
                final CustomEnchantment enchant = CustomEnchantmentAPI.getEnchantment(name);
                final int level = LoreReader.parseEnchantmentLevel(line);
                if (level > 0) {
                	list.put(enchant, level);
                }
            }

            // Short-circuit if we aren't finding valid formatted enchantments
            // since all enchantments should be added at the top.
            else if (name.isEmpty()) {
                return list;
            }
        }
        return list;
    }
    
    /**
     * @param items list of item to grab the enchantments from
     * @return list of custom enchantments (does not include vanilla enchantments)
     */
    public static Map<CustomEnchantment, Integer> getCustomEnchantments(final List<ItemStack> items) {

        final HashMap<CustomEnchantment, Integer> list = new HashMap<CustomEnchantment, Integer>();
        for (ItemStack item : items) {
        	final HashMap<CustomEnchantment, Integer> list1 = new HashMap<CustomEnchantment, Integer>();
        	list1.putAll(getCustomEnchantments(item));
        	list1.forEach((k, v) -> list.merge(k, v, (v1, v2) -> k.canStack() ? v1 += v2 : Math.max(v1, v2)));	
        }
        return list;
    }
    
    /**
     * Checks whether or not the item has the enchantment on it
     *
     * @param item item to check
     * @param enchantmentName name of the enchantment to check for
     * @return true if it has the enchantment, false otherwise
     */
    public static boolean hasCustomEnchantment(final ItemStack item, final String enchantmentName) {
        if (!item.hasItemMeta()) return false;
        final ItemMeta meta = item.getItemMeta();
        return meta.hasLore() && meta.getLore().stream().anyMatch(LoreReader::isEnchantment);
    }

    /**
     * Removes all enchantments from the item, including vanilla enchantments
     *
     * @param item item to remove all enchantments from
     * @return item with all enchantments removed
     */
    public static ItemStack removeAllEnchantments(final ItemStack item) {
        item.getEnchantments().forEach((enchant, level) -> item.removeEnchantment(enchant));
        return removeCustomEnchantments(item);
    }

    /**
     * Removes all custom enchantments from an item
     *
     * @param item item to remove enchantments from
     * @return item without custom enchantments
     */
    public static ItemStack removeCustomEnchantments(final ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
        if (meta.hasLore()) {
            meta.setLore(meta.getLore().stream()
                    .filter(line -> !LoreReader.isEnchantment(line))
                    .collect(Collectors.toList()));
        }
        item.setItemMeta(meta);
        return item;
    }
	
    /**
     * Removes a custom enchantment from an item
     *
     * @param item item to remove a custom enchantment from
     * @param name custom enchantment name to remove.
     * @return item without custom enchantments
     */
    public static ItemStack removeCustomEnchantment(final ItemStack item, String name) {
        final ItemMeta meta = item.getItemMeta();
        if (meta.hasLore()) {
            meta.setLore(meta.getLore().stream()
                    .filter(line -> !LoreReader.parseEnchantmentName(line).equals(name))
                    .collect(Collectors.toList()));
        }
        item.setItemMeta(meta);
        return item;
    }
	
}
