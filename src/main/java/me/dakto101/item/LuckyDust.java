package me.dakto101.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.netty.util.internal.ThreadLocalRandom;

public class LuckyDust extends Item {
	

	//chance in percent (0-100)
	private double chance;
	private ItemQuality quality;
	
	private static final String CHANCE = "§aXác suất cộng thêm (%): §7";
	private static final String QUALITY = "§aPhẩm chất: ";
	private static final String VIEW_CHANCE = "§7Click phải để xem xác suất cộng thêm.";
	
	public LuckyDust() {
		super(ItemType.LUCKY_DUST, Arrays.asList(
			"§7Một loại vật phẩm bổ trợ trong phù phép cao cấp, ",
			"§7giúp tăng xác suất thành công khi ép sách phù phép."
		), Material.GLOWSTONE_DUST);	
		this.quality = ItemQuality.COMMON;
		this.chance = 1d;
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
		for (String s : itemLore) {
			
			if (s.contains(QUALITY)) quality = true;
		}
		if (quality == false) return false;
		if (!itemLore.containsAll(getDescription())) return false;
		
		return true;
	}
	
	/**
	 * @return the chance
	 */
	public double getChance() {
		return chance;
	}
	
	/**
	 * 
	 */
	public void setChance(ItemQuality quality) {
		switch (this.quality) {
		case COMMON: {
			this.chance = 1;
			break;
		}
		case UNCOMMON: {
			this.chance = 2;
			break;
		}
		case RARE: {
			this.chance = 5;
			break;
		}	
		case ELITE: {
			this.chance = 10;
			break;
		}
		case MYSTERY: {
			this.chance = 20;
			break;
		}
		case LEGENDARY: {
			this.chance = 25;
			break;
		}
		case RANDOM: {
			this.chance = ThreadLocalRandom.current().nextInt(1, 31);
			break;
		}
		default: {
			break;
		}
		}
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
		this.setChance(quality);
	}

    // --- Functional Methods --- //
	
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
			if (s.contains(CHANCE)) {
				s = s.replace(CHANCE, "");
				this.chance = Double.parseDouble(s);
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
		itemLore.add(QUALITY + this.getQuality().getName());
		if (!this.getQuality().equals(ItemQuality.RANDOM)) itemLore.add(CHANCE + this.getChance());
		else itemLore.add(VIEW_CHANCE);
		//
		itemMeta.setLore(itemLore);
		item.setItemMeta(itemMeta);
		return item;
	}
	
	@Override
	public void click(HumanEntity p, ItemStack clickedItem) {
		if (!this.getQuality().equals(ItemQuality.RANDOM)) return;
		for (String s : clickedItem.getItemMeta().getLore()) {
			if (s.contains(CHANCE)) return;
		}
		
		this.chance = ThreadLocalRandom.current().nextInt(1, 31);
		
		ItemStack itemAdd = this.createItem();
		ItemMeta itemAddMeta = itemAdd.getItemMeta();
		List<String> itemAddLore = new ArrayList<String>();
		itemAddLore.addAll(itemAddMeta.getLore());
		itemAddLore.replaceAll(s -> s = s.replaceFirst(VIEW_CHANCE, CHANCE + this.getChance()));
		itemAddMeta.setLore(itemAddLore);
		itemAdd.setItemMeta(itemAddMeta);
		
		clickedItem.setAmount(clickedItem.getAmount() - 1);
		p.getInventory().setItemInMainHand(clickedItem);
		p.getInventory().addItem(itemAdd);
		p.getWorld().playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
		p.getWorld().spawnParticle(Particle.CLOUD, p.getLocation(), 50);
		p.sendMessage("§aBạn đã nhận được " + this.getName() + "§r§a.");
	}
	/**
	 * @return ItemStack → toString.
	 */
	@Override
	public String toString() {
		return createItem().toString();
	}






    
}
