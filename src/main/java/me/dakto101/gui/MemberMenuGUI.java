package me.dakto101.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.dakto101.HCraftEnchantment;

public class MemberMenuGUI {

	public static final String MENU = "§b§1§6§9§l§a§4§2§d§r§1§lDanh mục - Phù phép";
	
	public static Inventory memberMenu;
	
	public static void register() {
		registerEnchantmentTypeGUI();
	}
	
	private static void registerEnchantmentTypeGUI() {
		
		Inventory inv = HCraftEnchantment.plugin.getServer().createInventory(null, 9, MENU);
		
		ItemStack enchantmentList = new ItemStack(Material.FILLED_MAP, 1);
		ItemMeta enchantmentListMeta = enchantmentList.getItemMeta();
		enchantmentListMeta.setDisplayName("§a§lDanh sách phù phép");
		List<String> enchantmentListLore = new ArrayList<String>();
		enchantmentListLore.add("§7Click vào để xem danh mục các loại phù phép.");
		enchantmentListMeta.setLore(enchantmentListLore);
		enchantmentList.setItemMeta(enchantmentListMeta);
		
		ItemStack enchant = new ItemStack(Material.ENCHANTING_TABLE, 1);
		ItemMeta enchantMeta = enchant.getItemMeta();
		enchantMeta.setDisplayName("§b§lBàn phù phép cao cấp");
		List<String> enchantLore = new ArrayList<String>();
		enchantLore.add("§7Click vào để mở.");
		enchantMeta.setLore(enchantLore);
		enchant.setItemMeta(enchantMeta);
		
		ItemStack enchantmentCheck = new ItemStack(Material.WRITABLE_BOOK, 1);
		ItemMeta enchantmentCheckMeta = enchantmentCheck.getItemMeta();
		enchantmentCheckMeta.setDisplayName("§c§lXem thông tin phù phép");
		List<String> enchantmentCheckLore = new ArrayList<String>();
		enchantmentCheckLore.add("§7Click vào để xem thông tin phù phép trên trang bị của bạn.");
		enchantmentCheckMeta.setLore(enchantmentCheckLore);
		enchantmentCheck.setItemMeta(enchantmentCheckMeta);
		
		ItemStack blank = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemMeta blankMeta = blank.getItemMeta();
		blankMeta.setDisplayName(" ");
		blank.setItemMeta(blankMeta);
		
		for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, blank);
		inv.setItem(0, enchantmentList);
		inv.setItem(1, enchant);
		inv.setItem(2, enchantmentCheck);
		
		memberMenu = inv;
		
	}

	public static void click(HumanEntity p, int slot) {
		if (p == null) return;
		p.getWorld().playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
		
		switch (slot) {
		case 0: {
			p.openInventory(EnchantmentListGUI.enchantmentType);
			break;
		}
		case 1: {
			SuperEnchantmentTableGUI.open(p);
			break;
		}
		case 2: {
			EnchantmentCheckGUI.open(p);
			break;
		}

		}
		//??
	}
			
		
	
	
}
