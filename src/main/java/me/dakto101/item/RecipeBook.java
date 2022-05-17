package me.dakto101.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.dakto101.api.PlayerSkill;
import me.dakto101.api.Skill;
import me.dakto101.api.SkillAPI;
import me.dakto101.api.SkillEnum;
import me.dakto101.api.SkillType;

public class RecipeBook extends Item {
	
	private SkillType skillType;
	private Skill skill;
	private int xp;
	private ItemQuality quality;
	
	private static final String SKILL_TYPE = "§aLoại sách: §7";
	private static final String SKILL = "§aKỹ năng: §7";
	private static final String QUALITY = "§aPhẩm chất: ";
	private static final String XP = "§aĐiểm kinh nghiệm: §7";
	
	public RecipeBook() {
		super(ItemType.RECIPE_BOOK, Arrays.asList(
			"§7Bí kíp giúp người chơi tăng điểm kinh nghiệm cho các ",
			"§7kỹ năng kích hoạt. Chỉ dùng được khi đã học kỹ năng.",
			"§7Click phải để dùng."
		), Material.BOOK);	
		this.skillType = SkillType.SWORDSMANSHIP;
		this.skill = SkillAPI.getSkill(SkillEnum.GIAO_CHIEN);
		this.xp = 0;
		this.quality = ItemQuality.RANDOM;
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
		boolean quality = false;
		boolean xp = false;
		for (String s : itemLore) {
			if (s.contains(SKILL_TYPE)) skillType = true;
			if (s.contains(SKILL)) skill = true;
			if (s.contains(QUALITY)) quality = true;
			if (s.contains(XP)) xp = true;
		}
		if (!quality || !skillType || !skill || !xp) return false;
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
	@Deprecated
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

	/**
	 * @return the xp
	 */
	public int getXp() {
		return xp;
	}

	/**
	 * @param xp the xp to set
	 */
	public void setXp(int xp, boolean autoSetQuality) {
		this.xp = xp;
		
		if (autoSetQuality) {
			ItemQuality quality = ItemQuality.UNCOMMON;
			if (xp >= 3) quality = ItemQuality.RARE;
			if (xp >= 6) quality = ItemQuality.ELITE;
			if (xp >= 15) quality = ItemQuality.MYSTERY;
			if (xp >= 30) quality = ItemQuality.LEGENDARY;
			
			
			this.quality = quality;
		}
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
			if (s.contains(SKILL)) {
				s = s.replace(SKILL, "");
				Skill skill = SkillAPI.getSkill(s);
				this.setSkill(skill);
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
			if (s.contains(XP)) {
				s = s.replace(XP, "");
				try {
					this.setXp(Integer.parseInt(s), true);
				} catch (Exception e) {
					this.setXp(0, true);
				}
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
		itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
		//Lore
		List<String> itemLore = new ArrayList<String>();
		itemLore.add("");
		itemLore.addAll(this.getDescription());
		itemLore.add("");
		itemLore.add(SKILL_TYPE + this.getSkillType().getName());
		itemLore.add(SKILL + this.getSkill().getName());
		itemLore.add(QUALITY + this.getQuality().getName());
		itemLore.add(XP + this.getXp());
		
		//
		itemMeta.setLore(itemLore);
		item.setItemMeta(itemMeta);
		return item;
	}
	
	@Override
	public void click(HumanEntity p, ItemStack clickedItem) {
		if (!(p instanceof Player)) return;
		if (!SkillAPI.getPlayerSkills((Player) p).contains(this.getSkill())) {
			p.sendMessage("§c§l(!)§r §cBạn phải học kỹ năng §e" + this.getSkill().getName() + "§c mới có thể sử dụng sách này.");
			p.getWorld().playSound(p.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
			return;
		}
		PlayerSkill ps = new PlayerSkill((Player) p);
		ps.addSkillXP(this.getXp(), this.getSkill(), true);
		//Replace item in hand
		clickedItem.setAmount(clickedItem.getAmount() - 1);
		p.getInventory().setItemInMainHand(clickedItem);
		p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
		p.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, p.getLocation(), 50);
	}
	/**
	 * @return ItemStack → toString.
	 */
	@Override
	public String toString() {
		return createItem().toString();
	}






    
}
