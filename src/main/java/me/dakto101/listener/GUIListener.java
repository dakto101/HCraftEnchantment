package me.dakto101.listener;

import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import me.dakto101.gui.AdminMenuGUI;
import me.dakto101.gui.EnchantmentCheckGUI;
import me.dakto101.gui.EnchantmentListGUI;
import me.dakto101.gui.ItemListGUI;
import me.dakto101.gui.MemberMenuGUI;
import me.dakto101.gui.PlayerSkillDetailGUI;
import me.dakto101.gui.PlayerSkillGUI;
import me.dakto101.gui.SkillListGUI;
import me.dakto101.gui.SuperEnchantmentTableGUI;

public class GUIListener implements Listener {
	
    /**
     * When player open EnchantmentListGUI
     */
	@EventHandler
	public static void enchantmentTypeGUI(InventoryClickEvent e) {
		Inventory inv = e.getClickedInventory();
		if (!EnchantmentListGUI.enchantmentType.equals(inv)) {
			return;
		} else {
			e.setCancelled(true);
		}
		if (!inv.getType().equals(InventoryType.CHEST)) return;

		EnchantmentListGUI.click(e.getWhoClicked(), e.getSlot());
	}
	
    /**
     * When player open EnchantmentListGUI
     */
	@EventHandler
	public static void enchantmentTypeDetailsGUI(InventoryClickEvent e) {
		Inventory inv = e.getClickedInventory();
		if (!EnchantmentListGUI.enchantmentTypeDetails.containsValue(inv)) {
			return;
		} else {
			e.setCancelled(true);
		}
		if (!inv.getType().equals(InventoryType.CHEST)) return;

		int slot = e.getSlot();
		HumanEntity p = e.getWhoClicked();
		
		switch (slot) {
		case 53: {
			p.openInventory(EnchantmentListGUI.enchantmentType);
			p.getWorld().playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
			break;
		}
		default: {
			if (inv.getItem(slot) != null) p.getWorld().playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
			else
			p.getWorld().playSound(p.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
		}
		}

	}
    /**
     * When player open MemberMenu
     */
	@EventHandler
	public static void memberMenuGUI(InventoryClickEvent e) {
		Inventory inv = e.getClickedInventory();
		if (!MemberMenuGUI.memberMenu.equals(inv)) {
			return;
		} else {
			e.setCancelled(true);
		}
		if (!inv.getType().equals(InventoryType.CHEST)) return;
		//
		MemberMenuGUI.click(e.getWhoClicked(), e.getSlot());
	}
    /**
     * When player open AdminMenu
     */
	@EventHandler
	public static void adminMenuGUI(InventoryClickEvent e) {
		Inventory inv = e.getClickedInventory();
		if (!AdminMenuGUI.adminMenu.equals(inv)) {
			return;
		} else {
			e.setCancelled(true);
		}
		if (!inv.getType().equals(InventoryType.CHEST)) return;
		//
		AdminMenuGUI.click(e.getWhoClicked(), e.getSlot());
	}
    /**
     * When player open ItemListGUI
     */
	@EventHandler
	public static void itemListGUI(InventoryClickEvent e) {
		Inventory inv = e.getClickedInventory();
		if (!ItemListGUI.itemListGUI.equals(inv)) {
			return;
		} else {
			e.setCancelled(true);
		}
		if (!inv.getType().equals(InventoryType.CHEST)) return;
		//
		ItemListGUI.click(e.getWhoClicked(), e.getSlot());
	}
    /**
     * When player open EnchantmentCheckGUI
     */
	@EventHandler
	public static void enchantmentCheckGUI(InventoryClickEvent e) {
		Inventory inv = e.getClickedInventory();
		if (!e.getView().getTitle().equals(EnchantmentCheckGUI.MENU)) {
			return;
		} else {
			e.setCancelled(true);
		}
		if (inv.getType().equals(InventoryType.PLAYER) || (inv.getType().equals(InventoryType.CHEST) && e.getSlot() == 53)) {
			EnchantmentCheckGUI.click(e.getWhoClicked(), e.getSlot());
		}

		//
		
	}
    /**
     * When player open SuperEnchantmentTableGUI
     */
	@EventHandler
	public static void superEnchantmentTableGUI(InventoryClickEvent e) {
		Inventory inv = e.getClickedInventory();
		HumanEntity clicker = e.getWhoClicked();
		if (!e.getView().getTitle().equals(SuperEnchantmentTableGUI.SUPER_ENCHANTMENT_TABLE)) return;
		else if (!inv.getType().equals(InventoryType.CHEST)) return;
		else e.setCancelled(true);
		//
		if ((e.getSlot() == 10) || (e.getSlot() == 13) || (e.getSlot() == 16)) {
			e.setCancelled(false);
		}
		SuperEnchantmentTableGUI.click(clicker, e);
	}
	
    /**
     * When player open PlayerSkillGUI
     */
	@EventHandler
	public static void playerSkillGUI(InventoryClickEvent e) {
		Inventory inv = e.getClickedInventory();
		if (!e.getView().getTitle().equals(PlayerSkillGUI.PLAYER_SKILL)) {
			return;
		} else {
			e.setCancelled(true);
		}
		if (!inv.getType().equals(InventoryType.CHEST)) return;
		//
		PlayerSkillGUI.click(e.getWhoClicked(), e.getSlot());
	}
	
    /**
     * When player open PlayerSkillDetailGUI
     */
	@EventHandler
	public static void playerSkillDetailGUI(InventoryClickEvent e) {
		Inventory inv = e.getClickedInventory();
		if (!e.getView().getTitle().equals(PlayerSkillDetailGUI.PLAYER_SKILL_DETAIL)) {
			return;
		} else {
			e.setCancelled(true);
		}
		if (!inv.getType().equals(InventoryType.CHEST)) return;
		//
		PlayerSkillDetailGUI.click(e.getWhoClicked(), e.getSlot());
	}
	
    /**
     * When player close SuperEnchantmentTable
     */
	@EventHandler
	public static void superEnchantmentTableGUI(InventoryCloseEvent e) {
		Inventory inv = e.getInventory();
		if (!e.getView().getTitle().equals(SuperEnchantmentTableGUI.SUPER_ENCHANTMENT_TABLE)) return; 
		else if (!inv.getType().equals(InventoryType.CHEST)) return;
		//
		if (inv.getItem(10) != null) e.getPlayer().getInventory().addItem(inv.getItem(10));
		if (inv.getItem(13) != null) e.getPlayer().getInventory().addItem(inv.getItem(13));
		if (inv.getItem(16) != null) e.getPlayer().getInventory().addItem(inv.getItem(16));
	}
	
    /**
     * When player open SkillListGUI
     */
	@EventHandler
	public static void skillListGUI(InventoryClickEvent e) {
		Inventory inv = e.getClickedInventory();
		if (!e.getView().getTitle().equals(SkillListGUI.SKILL_LIST)) {
			return;
		} else {
			e.setCancelled(true);
		}
		if (!inv.getType().equals(InventoryType.CHEST)) return;
		//
		SkillListGUI.click(e.getWhoClicked(), e.getSlot());
	}
	
	
}
