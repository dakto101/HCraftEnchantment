package me.dakto101.gui;

import static me.dakto101.api.CustomEnchantmentType.DEFENSE;
import static me.dakto101.api.CustomEnchantmentType.MELEE;
import static me.dakto101.api.CustomEnchantmentType.OTHER;
import static me.dakto101.api.CustomEnchantmentType.POTION;
import static me.dakto101.api.CustomEnchantmentType.RANGED;
import static me.dakto101.api.CustomEnchantmentType.ULTILITY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.dakto101.HCraftEnchantment;
import me.dakto101.api.CustomEnchantmentAPI;
import me.dakto101.api.CustomEnchantmentType;
import me.dakto101.util.LoreReader;

public class EnchantmentListGUI {

	public static final String ENCHANTMENT_TYPE = "§3§a§7§8§b§5§4§6§d§1§lPhân loại phù phép";
	public static final String ENCHANTMENT_TYPE_DETAIL = "§3§a§7§8§b§5§4§6§d§1§lDanh sách phù phép";
	
	public static Inventory enchantmentType;
	public static Map<CustomEnchantmentType, Inventory> enchantmentTypeDetails = 
			new HashMap<CustomEnchantmentType, Inventory>();
	
	public static void register() {
		registerEnchantmentTypeGUI();
		registerEnchantmentTypeDetailGUI(
			DEFENSE, MELEE,
			POTION, RANGED, ULTILITY, OTHER
		);
	}
	
	public static void click(HumanEntity player, int slot) {
		CustomEnchantmentType type = null;
		switch (slot) {
		case 0: type = DEFENSE; break;
		case 1: type = MELEE; break;
		case 2: type = POTION; break;
		case 3: type = RANGED; break;
		case 4: type = ULTILITY; break;
		case 5: type = OTHER; break;
		}
		
		if (type != null) {
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1, 1);
			player.openInventory(EnchantmentListGUI.enchantmentTypeDetails.get(type));
		} else player.getWorld().playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
		
	}
	
	private static void registerEnchantmentTypeGUI() {
		
		Inventory inv = HCraftEnchantment.plugin.getServer().createInventory(null, 9, ENCHANTMENT_TYPE);
		
		for (int i = 0; i < CustomEnchantmentType.values().length; i++) {
			String enchantmentType = "";
			switch (i) {
			case 0: enchantmentType += DEFENSE.getName(); break;
			case 1: enchantmentType += MELEE.getName(); break;
			case 2: enchantmentType += POTION.getName(); break;
			case 3: enchantmentType += RANGED.getName(); break;
			case 4: enchantmentType += ULTILITY.getName(); break;
			case 5: enchantmentType += OTHER.getName(); break;
			default: enchantmentType = "§cError!";
			}
			
			ItemStack enchantmentTypeItem = new ItemStack(Material.BOOK, 1);
			ItemMeta enchantmentTypeItemMeta = enchantmentTypeItem.getItemMeta();
			enchantmentTypeItemMeta.setDisplayName("§a§l" + enchantmentType);
			List<String> enchantmentTypeItemLore = new ArrayList<String>();
			enchantmentTypeItemLore.add("§7Click vào để xem chi tiết thông tin loại phù phép.");
			enchantmentTypeItemMeta.setLore(enchantmentTypeItemLore);
			enchantmentTypeItem.setItemMeta(enchantmentTypeItemMeta);
			
			inv.setItem(i, enchantmentTypeItem);
		}
		
		enchantmentType = inv;
		
	}
	
	private static void registerEnchantmentTypeDetailGUI(CustomEnchantmentType... typeList) {
		for (final CustomEnchantmentType type : typeList) {
			Inventory inv = HCraftEnchantment.plugin.getServer().createInventory(null, 54, ENCHANTMENT_TYPE_DETAIL);
			//
			ItemStack other = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
			ItemMeta otherMeta = other.getItemMeta();
			otherMeta.setDisplayName(" ");
			other.setItemMeta(otherMeta);
			//
			for (int i = 45; i < 54; i++) {
				if (i == 53) {
					otherMeta.setDisplayName("§7Quay lại");
					other.setItemMeta(otherMeta);
					other.setType(Material.ARROW);
				}
				inv.setItem(i, other);
			}
			CustomEnchantmentAPI.CUSTOM_ENCHANTMENT.values()
			.stream().filter(ce -> ce.getType().equals(type))
			.collect(Collectors.toList()).forEach(ce -> {
				ItemStack enchInfo = new ItemStack(Material.ENCHANTED_BOOK, 1);
				ItemMeta enchInfoMeta = enchInfo.getItemMeta();
				enchInfoMeta.setDisplayName("§6" + ce.getName());
				List<String> lore = new ArrayList<String>();
				lore.add("§fMô tả: ");
				lore.add("");
				lore.addAll(LoreReader.formatDescription(ce.getDescription(), 10));
				lore.add("");
				lore.add("§fThuộc tính: ");
				lore.add("");
				lore.add("§7- Có thể cộng dồn cấp: §f" + (ce.canStack() ? "Có" : "Không"));
				lore.add("§7- Điểm thức ăn tiêu thụ: §f" + ce.getFoodRequire());
				lore.add("§7- Hồi chiêu (giây): §f" + ce.getCooldown());
				lore.add("§7- Độ hiếm: §f" + ce.getRarity());
				enchInfoMeta.setLore(lore);
				enchInfo.setItemMeta(enchInfoMeta);
				
				inv.addItem(enchInfo);

			});
			
			enchantmentTypeDetails.putIfAbsent(type, inv);
		}
			
		
	}
	
}
