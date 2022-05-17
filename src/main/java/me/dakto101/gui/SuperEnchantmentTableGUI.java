package me.dakto101.gui;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.dakto101.HCraftEnchantment;
import me.dakto101.event.HCraftEnchantItemEvent;

public class SuperEnchantmentTableGUI {

	public static final String SUPER_ENCHANTMENT_TABLE = "§2§4§5§a§6§b§8§c§e§6§3§lBàn phù phép cao cấp";
	
	public static void open(HumanEntity player) {
		
		Inventory inv = HCraftEnchantment.plugin.getServer().createInventory(null, 54, SUPER_ENCHANTMENT_TABLE);
		
		ItemStack pane = new ItemStack(Material.PINK_STAINED_GLASS, 1);
		ItemMeta paneMeta = pane.getItemMeta();
		paneMeta.setDisplayName(" ");
		pane.setItemMeta(paneMeta);
		
		ItemStack button = new ItemStack(Material.PAPER);
		
		ItemMeta infoMeta = button.getItemMeta();
		infoMeta.setDisplayName("§e§lHướng dẫn sử dụng");
		infoMeta.setLore(Arrays.asList(
			"§cÔ viền đỏ: §7Chứa trang bị cần phù phép.",
			"§eÔ viền vàng: §7Chứa sách phù phép cao cấp.",
			"§aÔ viền xanh lá: §7Chứa vật phẩm phụ trợ. (không bắt buộc)"
		));
		ItemMeta enchantMeta = button.getItemMeta();
		enchantMeta.setDisplayName("§6§lPhù phép");
		enchantMeta.setLore(Arrays.asList(
			"§7Bấm vào để bắt đầu phù phép trang bị."
		));
		ItemMeta backMeta = button.getItemMeta();
		backMeta.setDisplayName("§f§lQuay lại");
		
		for (int i = 0; i < 54; i++) {
			//Design inv
			if (i < 27) {
				if (i % 9 < 3) pane.setType(Material.RED_STAINED_GLASS_PANE);
				if (i % 9 >= 3 && i % 9 < 6) pane.setType(Material.YELLOW_STAINED_GLASS_PANE);
				if (i % 9 >= 6 && i % 9 < 9) pane.setType(Material.GREEN_STAINED_GLASS_PANE);
			}
			if (i >= 27 && i < 36) pane.setType(Material.IRON_BARS);
			if (i >= 36) pane.setType(Material.WHITE_STAINED_GLASS_PANE);
			if (i != 10 && i != 13 && i != 16) inv.setItem(i, pane);
			
			if (i == 31) {
				button.setType(Material.PAPER);
				button.setItemMeta(infoMeta);
				inv.setItem(i, button);
			}
			if (i == 40) {
				button.setItemMeta(enchantMeta);
				button.setType(Material.ANVIL);
				inv.setItem(i, button);
			}
			if (i == 53) {
				button.setItemMeta(backMeta);
				button.setType(Material.ARROW);
				inv.setItem(i, button);
			}
			
		}
		
		
		player.openInventory(inv);

		
	}
	/**
	 * @param p player who clicks inventory
	 * @param slot clicked slot
	 */
	public static void click(HumanEntity p, int slot) {
		if (p == null) return;
		p.getWorld().playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
		
		switch (slot) {
		case 40: {
			ItemStack item = p.getOpenInventory().getItem(10);
			ItemStack book = p.getOpenInventory().getItem(13);
			ItemStack extra = p.getOpenInventory().getItem(16);
			if (item == null || book == null) return;
			HCraftEnchantItemEvent e = new HCraftEnchantItemEvent(p, item, book, extra);
			Bukkit.getServer().getPluginManager().callEvent(e);
			if (!e.isCancelled()) e.enchant();
			break;
		}
		case 53: {
			p.openInventory(MemberMenuGUI.memberMenu);
			p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1, 1);
			break;
		}

		}
		//??
	}
			
		
	
	
}
