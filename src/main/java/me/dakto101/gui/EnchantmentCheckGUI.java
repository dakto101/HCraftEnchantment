package me.dakto101.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.dakto101.HCraftEnchantment;
import me.dakto101.api.CustomEnchantmentAPI;
import me.dakto101.util.LoreReader;
import me.dakto101.util.RomanNumerals;

public class EnchantmentCheckGUI {

	public static final String MENU = "§a§6§2§4§8§k§r§b§e§r§c§lXem thông tin phù phép";
	
	public static Inventory enchantmentCheck;
	
	public static void register() {
		registerEnchantmentCheckGUI();
	}

	public static void unregister() {
		enchantmentCheck = null;
	}
	
	public static void open(HumanEntity player) {
		Inventory inv = HCraftEnchantment.plugin.getServer().createInventory(null, 54, MENU);
		inv.setContents(enchantmentCheck.getContents());
		player.openInventory(inv);
	}
	
	private static void registerEnchantmentCheckGUI() {
		
		Inventory inv = HCraftEnchantment.plugin.getServer().createInventory(null, 54, MENU);
		
		ItemStack blank = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemMeta blankMeta = blank.getItemMeta();
		blankMeta.setDisplayName(" ");
		blank.setItemMeta(blankMeta);
		
		ItemStack info = new ItemStack(Material.PAPER);
		ItemMeta infoMeta = info.getItemMeta();
		infoMeta.setDisplayName("§a§lHướng dẫn sử dụng");
		infoMeta.setLore(Arrays.asList(
			"§7Click trái vào trang bị cần xem để xem thông tin phù phép.",
			"§7Thông tin phù phép sẽ được liệt kê ở bên ô trống ở dưới."));
		info.setItemMeta(infoMeta);
		
		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta backMeta = back.getItemMeta();
		backMeta.setDisplayName("§f§lQuay lại");
		back.setItemMeta(backMeta);
		
		for (int i = 0; i < inv.getSize(); i++) {
			if (i / 9 == 4 || i / 9 == 3) blank.setType(Material.AIR);
			if (i / 9 == 5) blank.setType(Material.GREEN_STAINED_GLASS_PANE);
			if (i == 27 || i == 35 || i == 36 || i == 44) blank.setType(Material.BLACK_STAINED_GLASS_PANE);
			inv.setItem(i, blank);
		}
		inv.setItem(13, info);
		inv.setItem(inv.getSize() - 1, back);
		
		enchantmentCheck = inv;
		
	}

	public static void click(HumanEntity p, int slot) {
		if (p == null) return;
		switch (slot) {
		case 53: {
			p.openInventory(MemberMenuGUI.memberMenu);
			break;
		}
		default: {
			EnchantmentCheckGUI.open(p);
			Inventory invBot = p.getOpenInventory().getBottomInventory();
			Inventory invTop = p.getOpenInventory().getTopInventory();
			ItemStack clickedItem = invBot.getItem(slot);
			CustomEnchantmentAPI.getCustomEnchantments(clickedItem).forEach((ce, level) -> {
				ItemStack enchInfo = new ItemStack(Material.ENCHANTED_BOOK, 1);
				ItemMeta enchInfoMeta = enchInfo.getItemMeta();
				enchInfoMeta.setDisplayName("§6" + ce.getName() + " " + RomanNumerals.toNumerals(level));
				List<String> lore = new ArrayList<String>();
				lore.add("§fMô tả: ");
				lore.add("");
				lore.addAll(LoreReader.formatDescription(ce.getDescription(level, p), 10));
				lore.add("");
				lore.add("§fThuộc tính: ");
				lore.add("");
				lore.add("§7- Loại phù phép: §f" + ce.getType().getName());
				lore.add("§7- Có thể cộng dồn cấp: §f" + (ce.canStack() ? "Có" : "Không"));
				lore.add("§7- Điểm thức ăn tiêu thụ: §f" + ce.getFoodRequire());
				lore.add("§7- Hồi chiêu (giây): §f" + ce.getCooldown());
				enchInfoMeta.setLore(lore);
				enchInfo.setItemMeta(enchInfoMeta);
				
				
				invTop.addItem(enchInfo);
			});
			invTop.setItem(13, invBot.getItem(slot));
			p.openInventory(invTop);
		}
		}
		p.getWorld().playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

		//??
	}
			
		
	
	
}
