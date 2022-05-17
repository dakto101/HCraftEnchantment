package me.dakto101.listener;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.dakto101.event.HCraftEnchantItemEvent;
import me.dakto101.item.Item;
import me.dakto101.item.ItemType;
import me.dakto101.item.LuckyDust;
import me.dakto101.item.MagicGem;
import me.dakto101.item.RandomEnchantedBook;
import me.dakto101.item.RecipeBook;
import me.dakto101.item.SkillBook;

public class ItemListener implements Listener {
	
    /**
     * When player RightClick on item in main hand.
     * Apply for item in main hand
     */
	@EventHandler
	public static void onRightClick(PlayerInteractEvent e) {
		if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			//Condition
			
			if (!e.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) return;
			ItemStack clickedItem = e.getPlayer().getInventory().getItemInMainHand();
			if ((clickedItem == null) || (clickedItem.getItemMeta() == null)) return;
			
			//Param
			String s = clickedItem.getItemMeta().getDisplayName();
	
			Item specialItem = null;
			//Code
			if (s.equals(ItemType.RANDOM_ENCHANTED_BOOK.getName())) specialItem = new RandomEnchantedBook();
			if (s.equals(ItemType.LUCKY_DUST.getName())) specialItem = new LuckyDust();
			if (s.equals(ItemType.MAGIC_GEM.getName())) specialItem = new MagicGem();
			if (s.equals(ItemType.RECIPE_BOOK.getName())) specialItem = new RecipeBook();
			if (s.equals(ItemType.SKILL_BOOK.getName())) specialItem = new SkillBook();

			if ((specialItem != null) && (specialItem.isParsable(clickedItem))) {
				specialItem.parse(clickedItem);
				specialItem.click(e.getPlayer(), clickedItem);
			}

		}
		
	}
	
	/**
	 * When player using SuperEnchantmentTableGUI.
	 * @param e event
	 */
	@EventHandler
	public static void onEnchantItem(HCraftEnchantItemEvent e) {
	}

	
	
}
