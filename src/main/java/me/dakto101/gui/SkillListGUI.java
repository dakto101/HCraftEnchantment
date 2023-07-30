package me.dakto101.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.dakto101.HCraftEnchantment;
import me.dakto101.api.Skill;
import me.dakto101.api.SkillAPI;
import me.dakto101.api.SkillEnum;
import me.dakto101.api.SkillType;
import me.dakto101.item.SkillBook;

public class SkillListGUI {

	public static final String SKILL_LIST = "§2§4§f§6§k§l§1§5§d§a§r§c§lDanh sách kỹ năng";
	public static final String ID_LORE = "§7- ID: §f";
	public static Inventory playerSkillDetailGUI;
	
	public static void register() {
		registerSkillListGUI();
	}

	public static void unregister() { playerSkillDetailGUI = null; }
	
	public static void open(HumanEntity player, SkillType skillType) {
		if (!(player instanceof Player) || skillType == null) return;
		Inventory inv = HCraftEnchantment.plugin.getServer().createInventory(null, 54, SKILL_LIST);
		inv.setContents(playerSkillDetailGUI.getContents());
		
		for (SkillEnum skillEnum : SkillAPI.getSkills().keySet()) {
			Skill skill = SkillAPI.getSkills().get(skillEnum);
			if (skill != null && skill.getType().equals(skillType)) {
				String skillTypeName = "";
				switch (skill.getType()) {
				case ARCHERY:
					skillTypeName = "Cung";
					break;
				case MAGIC:
					skillTypeName = "Sách";
					break;
				case OTHER:
					skillTypeName = "?";
					break;
				case SWORDSMANSHIP:
					skillTypeName = "Kiếm, rìu";
					break;
				case UNARMED:
					skillTypeName = "Tay không";
					break;
				default:
					break;
				}
				ItemStack info = new ItemStack(skill.getIcon());
				ItemMeta infoMeta = info.getItemMeta();
				infoMeta.setDisplayName("§6" + skill.getName());
				List<String> infoLore = new ArrayList<String>();
				infoLore.add("§fMô tả:");
				infoLore.add("");
				infoLore.addAll(skill.getDescription());
				infoLore.add("");
				infoLore.add("§fThuộc tính:");
				infoLore.add("");
				infoLore.add(ID_LORE + skill.getId());
				infoLore.add("§7- Hồi chiêu: §f" + skill.getActiveCooldown());
				infoLore.add("§7- Điểm thức ăn tiêu thụ: §f" + skill.getFoodRequire());
				infoLore.add("§7- Dùng cho: §f" + skillTypeName);
				infoMeta.setLore(infoLore);
				info.setItemMeta(infoMeta);
				inv.addItem(info);
			}
		}
		player.openInventory(inv);
	}
	
	private static void registerSkillListGUI() {
		
		Inventory inv = HCraftEnchantment.plugin.getServer().createInventory(null, 54, SKILL_LIST);
		
		ItemStack blank = new ItemStack(Material.BONE);
		ItemMeta blankMeta = blank.getItemMeta();
		blankMeta.setDisplayName(" ");
		blank.setItemMeta(blankMeta);
		
		ItemStack info = new ItemStack(Material.PAPER);
		ItemMeta infoMeta = info.getItemMeta();
		infoMeta.setDisplayName("§a§lHướng dẫn sử dụng");
		infoMeta.setLore(Arrays.asList(
			"§7",
			"§7Thông tin kỹ năng sẽ được liệt kê ở bên ô trống ở dưới.",
			"§7Bấm để lấy sách kỹ năng."
		));
		
		info.setItemMeta(infoMeta);
		
		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta backMeta = back.getItemMeta();
		backMeta.setDisplayName("§f§lQuay lại");
		back.setItemMeta(backMeta);
		// Decorate GUI
		for (int i = 0; i < inv.getSize(); i++) {
			if (i / 9 == 1) blank.setType(Material.BLACK_STAINED_GLASS_PANE);
			if (i % 9 == 0 || i % 9 == 8) blank.setType(Material.BLACK_STAINED_GLASS_PANE);
			if (i / 9 == 5) blank.setType(Material.GREEN_STAINED_GLASS_PANE);
			inv.setItem(i, blank);
			blank.setType(Material.AIR);
		}
		// Skill type icon
		for (SkillType type : SkillType.values()) {
			ItemStack item = new ItemStack(Material.PAPER);
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setDisplayName("§e" + type.getName());
			itemMeta.setLore(Arrays.asList(
					"§7Bấm để xem danh sách kĩ năng về §e" + type.getName() + "§7."
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
		blank.setType(Material.RED_STAINED_GLASS_PANE);
		for (int i = 1; i < 8; i++) if (inv.getItem(i) == null) inv.setItem(i, blank);
		
		inv.setItem(13, info);
		inv.setItem(inv.getSize() - 1, back);
		
		playerSkillDetailGUI = inv;
		
	}

	public static void click(HumanEntity p, int slot) {
		
		p.getWorld().playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
		Inventory inv = p.getOpenInventory().getTopInventory();
		ItemStack clickedItem = inv.getItem(slot);
		if (slot / 9 == 0) {
			for (SkillType skillType : SkillType.values()) {
				if (clickedItem.getItemMeta().getDisplayName().contains(skillType.getName())) {
					SkillListGUI.open(p, skillType);
					break;
				}
			}
		}
		if (slot / 9 >= 2 && slot / 9 <= 4) {
			String itemName = inv.getItem(slot).getItemMeta().getDisplayName();
			itemName = itemName.replace("§6", "");
			Skill s = SkillAPI.getSkill(itemName);
			if (s != null) {
				SkillBook sb = new SkillBook();
				sb.setSkill(s);
				sb.setSkillType(s.getType());
				p.getInventory().addItem(sb.createItem());
			}
		}
		if (slot == inv.getSize() - 1) p.openInventory(AdminMenuGUI.adminMenu);
	}
}
