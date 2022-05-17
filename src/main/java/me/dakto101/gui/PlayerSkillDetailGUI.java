package me.dakto101.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.dakto101.HCraftEnchantment;
import me.dakto101.api.PlayerSkill;
import me.dakto101.api.Skill;
import me.dakto101.api.SkillAPI;
import me.dakto101.api.SkillType;

public class PlayerSkillDetailGUI {

	public static final String PLAYER_SKILL_DETAIL = "§2§3§f§r§6§a§d§c§c§lChi tiết kỹ năng";
	public static final String ID_LORE = "§7- ID: §f";
	public static Inventory playerSkillDetailGUI;
	
	public static void register() {
		registerPlayerSkillDetailGUI();
	}
	
	
	public static void open(HumanEntity player, SkillType skillType) {
		if (!(player instanceof Player)) return;
		Inventory inv = HCraftEnchantment.plugin.getServer().createInventory(null, 54, PLAYER_SKILL_DETAIL);
		inv.setContents(playerSkillDetailGUI.getContents());
		//Editing PlayerSkillDetailGUI...
		Player p = (Player) player;
		PlayerSkill ps = new PlayerSkill(p);
		ps.load(p);
		for (Integer index : ps.getPlayerSkills().keySet()) {
			Skill skill = ps.getPlayerSkills().get(index);
			if (!skill.getType().equals(skillType)) continue;
			String canUse = "";
			switch (skill.getType()) {
			case ARCHERY:
				canUse = "Cung, nỏ";
				break;
			case MAGIC:
				canUse = "Sách";
				break;
			case OTHER:
				canUse = "?";
				break;
			case SWORDSMANSHIP:
				canUse = "Kiếm, rìu";
				break;
			case UNARMED:
				canUse = "Tay không";
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
			infoLore.addAll(skill.getDescription(ps.getPlayerSkillLevel().get(index), player));
			infoLore.add("");
			infoLore.add("§fThuộc tính:");
			infoLore.add("");
			infoLore.add(ID_LORE + skill.getId());
			infoLore.add("§7- Cấp: §f" + ps.getPlayerSkillLevel().get(index));
			
			infoLore.add("§7- Điểm kinh nghiệm: §f" + (PlayerSkill.getRequireBook(ps.getSkillLevel(skill)) - ps.getRequireBook().get(index))
					+ " / §e" + PlayerSkill.getRequireBook(ps.getSkillLevel(skill)));
			infoLore.add("§7- Hồi chiêu: §f" + skill.getCooldown());
			infoLore.add("§7- Điểm thức ăn tiêu thụ: §f" + skill.getFoodRequire());
			infoLore.add("§7- Dùng cho: §f" + canUse);
			if (ps.getPlayerChosenSkill() != null && ps.getPlayerChosenSkill().equals(skill)) {
				infoLore.add("");
				infoLore.add("§aBạn đang lựa chọn kỹ năng này.");
				infoMeta.addEnchant(Enchantment.DURABILITY, 1, true);
			}
			infoMeta.setLore(infoLore);
			info.setItemMeta(infoMeta);
			inv.addItem(info);
		}
		player.openInventory(inv);
	}
	
	private static void registerPlayerSkillDetailGUI() {
		
		Inventory inv = HCraftEnchantment.plugin.getServer().createInventory(null, 54, PLAYER_SKILL_DETAIL);
		
		ItemStack blank = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemMeta blankMeta = blank.getItemMeta();
		blankMeta.setDisplayName(" ");
		blank.setItemMeta(blankMeta);
		
		ItemStack info = new ItemStack(Material.PAPER);
		ItemMeta infoMeta = info.getItemMeta();
		infoMeta.setDisplayName("§a§lHướng dẫn sử dụng");
		infoMeta.setLore(Arrays.asList(
			"§7",
			"§7Thông tin kỹ năng sẽ được liệt kê ở bên ô trống ở dưới.",
			"§7Bấm vào kỹ năng để chọn sử dụng."
		));
		
		info.setItemMeta(infoMeta);
		
		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta backMeta = back.getItemMeta();
		backMeta.setDisplayName("§f§lQuay lại");
		back.setItemMeta(backMeta);
		
		for (int i = 0; i < inv.getSize(); i++) {
			if (i / 9 <= 4 && i / 9 >= 1) blank.setType(Material.AIR);
			if (i / 9 == 5) blank.setType(Material.GREEN_STAINED_GLASS_PANE);
			if (i / 9 <= 4 && i / 9 >= 1 && (i % 9 == 0 || i % 9 == 8)) blank.setType(Material.BLACK_STAINED_GLASS_PANE);
			inv.setItem(i, blank);
		}
		inv.setItem(4, info);
		inv.setItem(inv.getSize() - 1, back);
		
		playerSkillDetailGUI = inv;
		
	}

	public static void click(HumanEntity p, int slot) {
		if (p == null) return;
		switch (slot) {
		case 53: {
			p.openInventory(PlayerSkillGUI.playerSkillGui);
			break;
		}
		default: 
			ItemStack clickedItem = p.getOpenInventory().getTopInventory().getItem(slot);
			if (clickedItem == null || clickedItem.getType().equals(Material.AIR)) break;
			if (clickedItem.getItemMeta() == null || clickedItem.getItemMeta().getLore() == null) break;
			if (!(p instanceof Player)) return;
			for (String s : clickedItem.getItemMeta().getLore()) {
				if (s.contains(ID_LORE)) {
					int skillID = Integer.parseInt(s.replace(ID_LORE, ""));
					PlayerSkill ps = new PlayerSkill((Player) p);
					if (ps.getPlayerChosenSkill() == null || 
							(ps.getPlayerChosenSkill() != null && ps.getPlayerChosenSkill().getId() != skillID)) {
						ps.setPlayerChosenSkill(SkillAPI.getSkill(skillID));
						p.sendMessage("§aBạn đã chọn kỹ năng §6" + ps.getPlayerChosenSkill().getName() + "§a.");
						ps.save((Player) p);
						p.getWorld().playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
						PlayerSkillDetailGUI.open(p, SkillAPI.getSkill(skillID).getType());
					} else {
						p.sendMessage("§aBạn đã huỷ chọn kỹ năng §6" + ps.getPlayerChosenSkill().getName() + "§a.");
						ps.setPlayerChosenSkill(null);
						ps.save((Player) p);
						p.getWorld().playSound(p.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
						PlayerSkillDetailGUI.open(p, SkillAPI.getSkill(skillID).getType());
					}

				}
			}
			break;
		}
		p.getWorld().playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

		//??
	}
}
