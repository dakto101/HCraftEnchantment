package me.dakto101.gui;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.dakto101.HCraftEnchantment;
import me.dakto101.api.SkillType;

public class PlayerSkillGUI {

	public static final String PLAYER_SKILL = "§e§6§2§f§a§k§1§8§7§4§2§r§4§lDanh sách kỹ năng của bạn";
	
	public static Inventory playerSkillGui;

	
	public static void register() {
		registerPlayerSkillGUI();
	}
	
	public static void click(HumanEntity player, int slot) {
		String itemName = player.getOpenInventory().getTopInventory().getItem(slot) != null 
				? player.getOpenInventory().getTopInventory().getItem(slot).getItemMeta().getDisplayName()
				: "";
		for (SkillType type : SkillType.values()) {
			if (itemName.contains(type.getName())) {
				PlayerSkillDetailGUI.open(player, type);
				player.getWorld().playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
			}
		}
	}

	private static void registerPlayerSkillGUI() {
		Inventory inv = HCraftEnchantment.plugin.getServer().createInventory(null, 9, PLAYER_SKILL);
		
		for (SkillType type : SkillType.values()) {
			ItemStack item = new ItemStack(Material.PAPER);
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setDisplayName("§e" + type.getName());
			itemMeta.setLore(Arrays.asList(
					"§7Bấm để xem các kĩ năng về §e" + type.getName() + "§7 của bạn.",
					"§7Mỗi người chỉ được chọn 1 kỹ năng để sử dụng."
			));
			item.setItemMeta(itemMeta);
			
			switch (type) {
			case ARCHERY:
				item.setType(Material.BOW);
				break;
			case MAGIC:
				item.setType(Material.BOOK);
				break;
			case OTHER:
				item.setType(Material.CARROT_ON_A_STICK);
				break;
			case SWORDSMANSHIP:
				item.setType(Material.IRON_SWORD);
				break;
			case UNARMED:
				item.setType(Material.RABBIT_FOOT);
				break;
			default:
				break;
			}
			inv.addItem(item);
		}
		
		playerSkillGui = inv;
	}
	
}
