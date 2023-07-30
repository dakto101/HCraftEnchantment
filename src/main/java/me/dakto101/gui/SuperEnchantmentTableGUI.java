package me.dakto101.gui;

import java.util.*;

import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentAPI;
import me.dakto101.item.AdvancedEnchantedBook;
import me.dakto101.item.Amulet;
import me.dakto101.item.LuckyDust;
import me.dakto101.item.MagicGem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.dakto101.HCraftEnchantment;
import me.dakto101.event.HCraftEnchantItemEvent;

public class SuperEnchantmentTableGUI {

	public static final String SUPER_ENCHANTMENT_TABLE = "§2§4§5§a§6§b§8§c§e§6§3§lBàn phù phép cao cấp";

	private static final int INVENTORY_SLOT_INFO = 31;
	private static final int INVENTORY_SLOT_ENCHANT = 40;
	private static final int INVENTORY_SLOT_BACK = 53;

	private static final String ITEM_ENCHANT_1 = "§7Điểm kinh nghiệm yêu cầu: §f";

	
	public static void open(HumanEntity player) {
		if (!(player instanceof Player)) return;

		Player p = (Player) player;
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
				"§aÔ viền xanh lá: §7Chứa vật phẩm phụ trợ. (không bắt buộc)",
				"§7Phù phép sẽ cần điểm kinh nghiệm. "

		));
		ItemMeta enchantMeta = button.getItemMeta();
		enchantMeta.setDisplayName("§6§lPhù phép");
		enchantMeta.setLore(Arrays.asList(
				"§7Điểm kinh nghiệm của bạn: §f" + p.getTotalExperience() ,
				ITEM_ENCHANT_1 + "0",
				"",
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
			
			if (i == INVENTORY_SLOT_INFO) {
				button.setType(Material.PAPER);
				button.setItemMeta(infoMeta);
				inv.setItem(i, button);
			}
			if (i == INVENTORY_SLOT_ENCHANT) {
				button.setItemMeta(enchantMeta);
				button.setType(Material.ANVIL);
				inv.setItem(i, button);
			}
			if (i == INVENTORY_SLOT_BACK) {
				button.setItemMeta(backMeta);
				button.setType(Material.ARROW);
				inv.setItem(i, button);
			}
			
		}
		
		
		player.openInventory(inv);

		
	}
	/**
	 * @param p player who clicks inventory
	 * @param event InventoryClickEvent
	 */
	public static void click(HumanEntity p, InventoryClickEvent event) {
		if (p == null) return;
		p.getWorld().playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

		int slot = event.getSlot();
		switch (slot) {
			// Hiển thị điểm kinh nghiệm yêu cầu của sách phép khi hover vào nút enchant.
			case 13: {
				ItemStack addedBook = event.getCursor() == null ? event.getCurrentItem() : event.getCursor();
				AdvancedEnchantedBook addedEnchantedBook = new AdvancedEnchantedBook();

				if (addedBook == null) return;
				if (addedEnchantedBook.isParsable(addedBook)) {

					ItemStack enchantButton = p.getOpenInventory().getItem(INVENTORY_SLOT_ENCHANT);
					ItemMeta enchantButtonMeta = enchantButton.getItemMeta();
					List<String> enchantButtonLore = enchantButtonMeta.getLore();

					addedEnchantedBook.parse(addedBook);
					for (int i = 0; i < enchantButtonLore.size(); i++) {
						if (enchantButtonLore.get(i).contains(ITEM_ENCHANT_1)) {
							enchantButtonLore.set(i, ITEM_ENCHANT_1 + addedEnchantedBook.getEnchantmentExpRequire());
						}
					}
					enchantButtonMeta.setLore(enchantButtonLore);
					enchantButton.setItemMeta(enchantButtonMeta);

				}
				break;
			}
			// Phù phép
			case INVENTORY_SLOT_ENCHANT: {
				ItemStack item = p.getOpenInventory().getItem(10);
				ItemStack book = p.getOpenInventory().getItem(13);
				ItemStack extra = p.getOpenInventory().getItem(16);
				if (item == null || book == null) return;
				HCraftEnchantItemEvent e = new HCraftEnchantItemEvent(p, item, book, extra);
				Bukkit.getServer().getPluginManager().callEvent(e);

				e.setCancelled(!check(p, item, book, extra));

				init(e);

				if (!e.isCancelled()) enchant(e);
				break;
			}
			// Quay lại
			case INVENTORY_SLOT_BACK: {
				p.openInventory(MemberMenuGUI.memberMenu);
				p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1, 1);
				break;
			}
			default: break;
		}
	}

	// --- Functional Methods --- //

	/** Check the condition before enchanting.
	 *
	 * @param p player who clicks the inventory.
	 * @param item need to enchant.
	 * @param book contains custom enchantment.
	 * @param extra item.
	 * @return true if satisfy the condition.
	 */
	private static boolean check(HumanEntity p, ItemStack item, ItemStack book, ItemStack extra) {
		AdvancedEnchantedBook bookInfo = new AdvancedEnchantedBook();
		if (p == null) return false;
		if (item == null || book == null || !bookInfo.isParsable(book)) {
			p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1, 1);
			p.sendMessage("§c§l(!) §r§cBạn cần phải thêm trang bị cần phù phép"
					+ " và sách phù phép cao cấp vào ô.");
			return false;
		}
		if (item.getAmount() > 1) {
			p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1, 1);
			p.sendMessage("§c§l(!) §r§cSố lượng vật phẩm cần phù phép đưa vào phải bằng §e1§c.");
			return false;
		}
		if (book.getAmount() > 1) {
			p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1, 1);
			p.sendMessage("§c§l(!) §r§cSố lượng sách đưa vào phải bằng §e1§c.");
			return false;
		}
		if (extra != null && extra.getAmount() > 1) {
			p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1, 1);
			p.sendMessage("§c§l(!) §r§cSố lượng vật phẩm bổ trợ đưa vào phải bằng §e1§c.");
			return false;
		}
		if (extra != null) {
			boolean parsable = false;
			if (new MagicGem().isParsable(extra)) parsable = true;
			if (new LuckyDust().isParsable(extra)) parsable = true;
			if (new Amulet().isParsable(extra)) parsable = true;
			if (!parsable) {
				p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1, 1);
				p.sendMessage("§c§l(!) §r§cVật phẩm bổ trợ không hợp lệ, hãy thay bằng vật phẩm hợp lệ hoặc bỏ trống.");
				return false;
			}
		}
		if (CustomEnchantmentAPI.getCustomEnchantments(item).size() > 0) {
			p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1, 1);
			p.sendMessage("§c§l(!) §r§cTrang bị này đã có phù phép cao cấp rồi.");
			return false;
		}
		return true;
	}

	/** Calculate param for event
	 *
	 * @param e HCraftEnchantItemEvent
	 */
	private static void init(HCraftEnchantItemEvent e) {
		if (e == null) return;
		//get chance from enchantedbook.
		AdvancedEnchantedBook enchantedBook = new AdvancedEnchantedBook();
		enchantedBook.parse(e.getBook());
		e.setChance(enchantedBook.getChance());
		// Extra item
		ItemStack extra = e.getExtraItem();

		if (extra == null) return;
		if (new Amulet().isParsable(extra)) {
			e.setRemoveEquipmentOnFail(false);
			return;
		}
		if (new LuckyDust().isParsable(extra)) {
			LuckyDust dust = new LuckyDust();
			dust.parse(extra);
			e.setChance(e.getChance() + dust.getChance());
			return;
		}
		if (new MagicGem().isParsable(extra)) {
			MagicGem gem = new MagicGem();
			gem.parse(extra);
			e.setExtraLevelChance(e.getExtraLevelChance() + gem.getChance());
			return;
		}

		//
	}

	/** Add custom enchantment from book to item.
	 *
	 * @param e
	 */
	private static void enchant(HCraftEnchantItemEvent e) {
		try {
			double chance = e.getChance();
			double extraLevelChance = e.getExtraLevelChance();
			ItemStack item = e.getEquipment();
			ItemStack book = e.getBook();
			HumanEntity p = e.getPlayer();
			//Check if success
			boolean success = chance*0.01 > Math.random();
			boolean extraLevelSuccess = extraLevelChance * 0.01 > Math.random();
			int extraLevel = (extraLevelSuccess ? 1 : 0);
			Map<CustomEnchantment, Integer> enchantments = CustomEnchantmentAPI.getCustomEnchantments(book);
			List<CustomEnchantment> enchantmentList = new ArrayList<CustomEnchantment>(enchantments.keySet());
			Collections.shuffle(enchantmentList);
			for (CustomEnchantment ce : enchantmentList) {
				if (success) {
					if (extraLevel > 0) {
						item = ce.addToItem(item, enchantments.get(ce) + extraLevel);
						extraLevel = 0;
					} else item = ce.addToItem(item, enchantments.get(ce));
				}
			}
			p.getOpenInventory().setItem(10, new ItemStack(Material.AIR));
			p.getOpenInventory().setItem(13, new ItemStack(Material.AIR));
			p.getOpenInventory().setItem(16, new ItemStack(Material.AIR));

			if (success) {
				p.getWorld().playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
				p.getInventory().addItem(item);
				p.sendMessage("§aPhù phép thành công.");
				if (extraLevelSuccess) {
					p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1.5f);
					p.sendMessage("§aBạn nhận được thêm 1 cấp phù phép cộng thêm từ vật phẩm bổ trợ.");
				}
			} else {
				p.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, p.getLocation(), 1);
				p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 0);
				p.getInventory().addItem(e.isRemoveEquipmentOnFail() ? new ItemStack(Material.AIR) : item);
				p.sendMessage("§c§l(!) §r§cPhù phép thất bại.");
			}

		} catch (Exception exception) {
			e.getPlayer().sendMessage("§cError...#" + SuperEnchantmentTableGUI.class.getName() + "#enchant");
			Bukkit.getServer().getLogger().info("§cError...#" + SuperEnchantmentTableGUI.class.getName() + "#enchant");
		}
	}

}
