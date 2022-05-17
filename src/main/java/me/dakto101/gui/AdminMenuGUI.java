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
import me.dakto101.api.SkillType;

public class AdminMenuGUI {

	public static final String MENU = "§2§4§e§6§r§a§b§e§8§9§r§2§lDanh mục (admin) - Phù phép";
	
	public static Inventory adminMenu;
	
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
		
		ItemStack skillList = new ItemStack(Material.GOLDEN_SWORD, 1);
		ItemMeta skillListMeta = skillList.getItemMeta();
		skillListMeta.setDisplayName("§b§lDanh sách kỹ năng");
		List<String> skillListLore = new ArrayList<String>();
		skillListLore.add("§7Click vào để xem danh mục các loại kỹ năng.");
		skillListMeta.setLore(skillListLore);
		skillList.setItemMeta(skillListMeta);
		
		ItemStack enchant = new ItemStack(Material.ENCHANTING_TABLE, 1);
		ItemMeta enchantMeta = enchant.getItemMeta();
		enchantMeta.setDisplayName("§c§lBàn phù phép cao cấp");
		List<String> enchantLore = new ArrayList<String>();
		enchantLore.add("§7Click vào để mở.");
		enchantMeta.setLore(enchantLore);
		enchant.setItemMeta(enchantMeta);
		
		ItemStack item = new ItemStack(Material.CHEST, 1);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName("§d§lVật phẩm phù phép");
		List<String> itemLore = new ArrayList<String>();
		itemLore.add("§7Click vào để xem danh sách một số vật phẩm phù phép ");
		itemLore.add("§7như sách, bùa phép...");
		itemMeta.setLore(itemLore);
		item.setItemMeta(itemMeta);
		
		
		
		ItemStack blank = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemMeta blankMeta = blank.getItemMeta();
		blankMeta.setDisplayName(" ");
		blank.setItemMeta(blankMeta);
		
		for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, blank);
		inv.setItem(0, enchantmentList);
		inv.setItem(1, skillList);
		inv.setItem(2, enchant);
		inv.setItem(3, item);
		
		adminMenu = inv;
		
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
			SkillListGUI.open(p, SkillType.ARCHERY);
			break;
		}
		case 2: {
			SuperEnchantmentTableGUI.open(p);
			break;
		}
		case 3: {
			p.openInventory(ItemListGUI.itemListGUI);
			break;
		}
		}
		//??
	}
			
		
	
	
}
