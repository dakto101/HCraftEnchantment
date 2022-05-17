package me.dakto101.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.netty.util.internal.ThreadLocalRandom;
import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentAPI;
import me.dakto101.api.CustomEnchantmentType;

public class RandomEnchantedBook extends Item {
	
	private ItemQuality quality;
	private List<CustomEnchantmentType> types;
	
	private static final String QUALITY = "§aPhẩm chất: ";
	private static final String TYPE = "§aLoại phù phép: §7";
	private static final double MIN_RARITY = 0;
	
	public RandomEnchantedBook() {
		super(ItemType.RANDOM_ENCHANTED_BOOK, Arrays.asList(
			"§7Sách chứa phù phép cao cấp ngẫu nhiên. ",
			"§7Click phải để mở."
		), Material.ENCHANTED_BOOK);	
		this.quality = ItemQuality.COMMON;
		this.types = Arrays.asList(CustomEnchantmentType.MELEE);
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
		boolean quality = false;
		boolean type = false;
		for (String s : itemLore) {
			if (s.contains(QUALITY)) quality = true;
			if (s.contains(TYPE)) type = true;
		}
		if ((quality == false) || (type == false)) return false;
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
	 * @return the type
	 */
	public List<CustomEnchantmentType> getTypes() {
		return types;
	}

	/**
	 * @param types the type to set
	 */
	public void setType(List<CustomEnchantmentType> types) {
		this.types = types;
	}

    // --- Functional Methods --- //
	/**
	 * 
	 */
	@Override
	public void click(HumanEntity p, ItemStack clickedItem) {
		//enchantmentList: CustomEnchantment and probability. Probability = 1/Rarity.
		Map<CustomEnchantment, Double> enchantmentList = new HashMap<CustomEnchantment, Double>();
		double total = 0;
		for (String enchantName : CustomEnchantmentAPI.CUSTOM_ENCHANTMENT.keySet()) {
			CustomEnchantment ce = CustomEnchantmentAPI.CUSTOM_ENCHANTMENT.get(enchantName);
			if (this.getTypes().isEmpty()) return;
			if ((ce != null) && (this.getTypes().contains(ce.getType())) && (ce.getRarity() > MIN_RARITY)) {
				enchantmentList.put(ce, 1.0/ce.getRarity());
				total += (1.0/ce.getRarity());
			}
		}

		final double total1 = total;
		enchantmentList.replaceAll((k, v) -> v *= (100/total1));
		total = 0;
		
		int maxTotalLevel = 0;
		switch (this.getQuality()) {
		//Enchantment level = 1→3
		//Level = 1→3, enchantments = 1→2
		case COMMON: {
			maxTotalLevel = 3;
			break;
		}
		//Level = 4→6, enchantments = random
		case UNCOMMON: {
			maxTotalLevel = 6;
			break;
		}
		//Level = 7→9, enchantments = random
		case RARE: {
			maxTotalLevel = 9;
			break;
		}
		//Level = 10→12, enchantments = random
		case ELITE: {
			maxTotalLevel = 12;
			break;
		}
		//Level = 13→15, enchantments = random
		case MYSTERY: {
			maxTotalLevel = 15;
			break;
		}
		//Level = 18, enchantments = random
		case LEGENDARY: {
			maxTotalLevel = 18;
			break;
		}
		//Level = 3→21, enchantments = random
		case RANDOM: {
			maxTotalLevel = ThreadLocalRandom.current().nextInt(3, 21);
		}
		default:
			break;
		}
		//Give book.
		Map<CustomEnchantment, Integer> result = generateEnchantmentList(enchantmentList, maxTotalLevel);
		AdvancedEnchantedBook givenBook = new AdvancedEnchantedBook();
		givenBook.setChance(Math.random()*100);
		givenBook.setEnchantments(result);
		givenBook.setQuality(this.getQuality());
		//Replace item in hand
		clickedItem.setAmount(clickedItem.getAmount() - 1);
		p.getInventory().setItemInMainHand(clickedItem);
		p.getInventory().addItem(givenBook.createItem());
		p.getWorld().playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
		p.getWorld().spawnParticle(Particle.CLOUD, p.getLocation(), 50);
		p.sendMessage("§aBạn đã nhận được " + givenBook.getName() + "§r§a.");
	}
	
	private static Map<CustomEnchantment, Integer> generateEnchantmentList(Map<CustomEnchantment, Double> enchantmentList, int maxTotalLevel) {
		Map<CustomEnchantment, Integer> result = new HashMap<CustomEnchantment, Integer>();
		int totalLevel = 0;
		for (int i = 1; i < 100; i++) {
			for (CustomEnchantment ce : enchantmentList.keySet()) {
				double chance = enchantmentList.get(ce);
				if (Math.random()*100 < chance) {
					//Random 1→3
					int random = ThreadLocalRandom.current().nextInt(1, 4);
					totalLevel += random;
					if (totalLevel > maxTotalLevel) return result;
					if (result.containsKey(ce)) {
						result.replace(ce, result.get(ce) + random);
					} else {
						result.put(ce, random);
					}
				}
			}
		}

		return result;
	}
	
	/**
	 * @param item must be advance enchanted book.
	 */
	@Override
	public void parse(final ItemStack item) {
		if (!this.isParsable(item)) return;
		for (String s : item.getItemMeta().getLore()) {
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
			if (s.contains(TYPE)) {
				s = s.replace(TYPE, "");
				this.setType(CustomEnchantmentType.toList(s));
			}
		}
	}

	/**
	 * @return a sample item.
	 */
	public static ItemStack createSampleItem() {
		ItemStack sample = new ItemStack(Material.ENCHANTED_BOOK);
		ItemMeta sampleMeta = sample.getItemMeta();
		sampleMeta.setDisplayName("" + ItemType.RANDOM_ENCHANTED_BOOK.getName());
		sampleMeta.setLore(Arrays.asList(
			"",
			"§7Sách chứa phù phép cao cấp ngẫu nhiên. ",
			"§7Click phải để mở.",
			"",
			QUALITY + ItemQuality.MYSTERY.getName(),
			TYPE + CustomEnchantmentType.toString(Arrays.asList(
				CustomEnchantmentType.MELEE,
				CustomEnchantmentType.POTION
			))
		));
		sample.setItemMeta(sampleMeta);
		
		return sample;
	}
	
	/**
	 * @return a random enchanted book
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
		itemLore.add(QUALITY + this.getQuality().getName());
		itemLore.add(TYPE + CustomEnchantmentType.toString(this.getTypes()));
		//
		itemMeta.setLore(itemLore);
		item.setItemMeta(itemMeta);
		return item;
	}

	/**
	 * @return ItemStack → toString.
	 */
	@Override
	public String toString() {
		return createItem().toString();
	}


    
}
