package me.dakto101.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.dakto101.api.PlayerSkill;
import me.dakto101.api.Skill;
import me.dakto101.api.SkillAPI;
import me.dakto101.api.SkillEnum;
import me.dakto101.api.SkillType;

public class SkillBook extends Item {
	
	private SkillType skillType;
	private Skill skill;
	private int skillID;
	private ItemQuality quality;
	
	private static final String SKILL_TYPE = "§aLoại sách: §7";
	private static final String SKILL_ID = "§aID: §7";
	private static final String SKILL = "§aKỹ năng: §7";
	private static final String QUALITY = "§aPhẩm chất: ";
	
	public SkillBook() {
		super(ItemType.SKILL_BOOK, Arrays.asList(
			"§7Sách dùng để học kỹ năng mới, cầm trên tay cuốn ",
			"§7sách và click phải để học kỹ năng. §e+1§7 điểm kỹ năng ",
			"§7nếu đã học kỹ năng từ trước."
		), Material.ENCHANTED_BOOK);	
		this.skillType = SkillType.SWORDSMANSHIP;
		this.skill = SkillAPI.getSkill(SkillEnum.GIAO_CHIEN);
		this.skillID = skill.getId();
		this.quality = ItemQuality.ELITE;
	}
	
    // ---- Getters/Setters ---- //

	
	/**
	 * @return a sample item.
	 */
	@Override
	public boolean isParsable(ItemStack item) {
		
		if (!item.getType().equals(this.material)) return false;
		if (!item.getItemMeta().getDisplayName().equals(this.getItemType().getName())) return false;
		
		List<String> itemLore = item.getItemMeta().getLore();
		boolean skillType = false;
		boolean skill = false;
		boolean skillID = false;
		boolean quality = false;
		for (String s : itemLore) {
			if (s.contains(SKILL_TYPE)) skillType = true;
			if (s.contains(SKILL)) skill = true;
			if (s.contains(SKILL_ID)) skillID = true;
			if (s.contains(QUALITY)) quality = true;
		}

		if (!quality || !skillType || !skill || !skillID) return false;
		if (!itemLore.containsAll(getDescription())) return false;
		
		return true;
	}

	/**
	 * @return the quality
	 */
	public ItemQuality getQuality() {
		return quality;
	}

	/**
	 * @param quality the quality to set
	 */
	public void setQuality(ItemQuality quality) {
		this.quality = quality;
	}
	
	/**
	 * @return the skill
	 */
	public Skill getSkill() {
		return skill;
	}

	/**
	 * @param skill the skill to set
	 */
	public void setSkill(Skill skill) {
		this.skill = skill;
		this.skillID = skill.getId();
	}
	
	/**
	 * 
	 * @return skill id
	 */
	public int getSkillID() {
		return skillID;
	}

	/**
	 * @return the skillType
	 */
	public SkillType getSkillType() {
		return skillType;
	}

	/**
	 * @param skillType the skillType to set
	 */
	public void setSkillType(SkillType skillType) {
		this.skillType = skillType;
	}

    // --- Functional Methods --- //
	
	/**
	 * @param item must be advance enchanted book.
	 */
	@Override
	public void parse(final ItemStack item) {
		if (!this.isParsable(item)) return;
		for (String s : item.getItemMeta().getLore()) {
			if (s.contains(SKILL_TYPE)) {
				s = s.replace(SKILL_TYPE, "");
				SkillType skillType = null;
				for (SkillType type : SkillType.values()) {
					if (type.getName().equals(s)) {
						skillType = type;
						break;
					}
				}
				this.setSkillType(skillType);
			}
			if (s.contains(SKILL_ID)) {
				s = s.replace(SKILL_ID, "");
				Skill skill = SkillAPI.getSkill(Integer.parseInt(s));
				this.setSkill(skill);
				this.skillID = skill.getId();
			}
			if (s.contains(QUALITY)) {
				s = s.replace(QUALITY, "");
				ItemQuality q = null;
				for (ItemQuality quality : ItemQuality.values()) {
					if (quality.getName().equals(s)) {
						q = quality;
						break;
					}
				}
				this.setQuality(q);
			}
		}
	}
	
	/**
	 * @return an advance enchanted book
	 */
	@Override
	public ItemStack createItem() {
		ItemStack item = new ItemStack(this.material);
		//Meta
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(this.getItemType().getName());
		//Lore
		List<String> itemLore = new ArrayList<String>();
		itemLore.add("");
		itemLore.addAll(this.getDescription());
		itemLore.add("");
		itemLore.add(SKILL_TYPE + this.getSkillType().getName());
		itemLore.add(SKILL_ID + this.getSkillID());
		itemLore.add(SKILL + this.getSkill().getName());
		itemLore.add(QUALITY + this.getQuality().getName());
		
		//
		itemMeta.setLore(itemLore);
		item.setItemMeta(itemMeta);
		return item;
	}
	
	@Override
	public void click(HumanEntity p, ItemStack clickedItem) {
		if (!(p instanceof Player)) return;
		PlayerSkill ps = new PlayerSkill((Player) p);
		if (ps.addSkill(this.getSkill())) {
			p.sendMessage("§aBạn đã học kỹ năng §e" + this.getSkill().getName() + "§a thành công.");
			p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
			p.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, p.getLocation(), 50);
		} else {
			ps.addSkillXP(1, this.getSkill(), true);
			p.getWorld().playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
		}
		//Replace item in hand
		clickedItem.setAmount(clickedItem.getAmount() - 1);
		p.getInventory().setItemInMainHand(clickedItem);
	}
	/**
	 * @return ItemStack → toString.
	 */
	@Override
	public String toString() {
		return createItem().toString();
	}







    
}
