package me.dakto101.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.netty.util.internal.ThreadLocalRandom;
import me.dakto101.HCraftEnchantment;
import me.dakto101.permission.HPermission;
import me.dakto101.api.CustomEnchantmentType;
import me.dakto101.item.Amulet;
import me.dakto101.item.ItemQuality;
import me.dakto101.item.LuckyDust;
import me.dakto101.item.MagicGem;
import me.dakto101.item.RandomEnchantedBook;
import me.dakto101.item.RecipeBook;
import me.dakto101.item.SkillBook;

public class ItemListGUI {

	public static final String ITEM_LIST = "§4§b§e§2§6§9§l§d§1§2§lVật phẩm bổ trợ";
	
	public static Inventory itemListGUI;

	
	public static void register() {
		registerItemListGUI();
	}
	
	public static void click(HumanEntity player, int slot) {
		if (player.hasPermission(HPermission.COMMAND_ADMIN.toString())) {
			switch (slot) {
			case 53: {
				player.openInventory(AdminMenuGUI.adminMenu);
				player.getWorld().playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
				break;
			}
			default: {
				ItemStack item = player.getOpenInventory().getItem(slot);
				if (item != null) {
					player.getInventory().addItem(item);
					player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
				}
			}
			}			
		}
	}

	private static void registerItemListGUI() {
		Inventory inv = HCraftEnchantment.plugin.getServer().createInventory(null, 54, ITEM_LIST);
		//Add RandomEnchantedBook
		for (ItemQuality q : ItemQuality.values()) {
			RandomEnchantedBook randomBook = new RandomEnchantedBook();
			randomBook.setQuality(q);
			List<CustomEnchantmentType> typeList = new ArrayList<CustomEnchantmentType>();
			for (CustomEnchantmentType t : CustomEnchantmentType.values()) {
				if (ThreadLocalRandom.current().nextInt(1, 2) == 1) typeList.add(t);
			}
			if (!typeList.isEmpty()) randomBook.setType(typeList);
			inv.addItem(randomBook.createItem());
		}
		//Add Amulet, LuckyDust, MagicGem
		inv.addItem(new Amulet().createItem());
		for (ItemQuality q : ItemQuality.values()) {
			LuckyDust dust = new LuckyDust();
			dust.setQuality(q);
			inv.addItem(dust.createItem());
			MagicGem gem = new MagicGem();
			gem.setQuality(q);
			inv.addItem(gem.createItem());
		}
		//Add recipe book
		RecipeBook rb = new RecipeBook();
		rb.setXp(ThreadLocalRandom.current().nextInt(1, 30), true);
		inv.addItem(rb.createItem());
		//Add skill book
		inv.addItem(new SkillBook().createItem());
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
		itemListGUI = inv;
	}
	
}
