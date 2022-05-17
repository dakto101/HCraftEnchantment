package me.dakto101.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentAPI;
import me.dakto101.item.AdvancedEnchantedBook;
import me.dakto101.item.Amulet;
import me.dakto101.item.LuckyDust;
import me.dakto101.item.MagicGem;

public class HCraftEnchantItemEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    
    private boolean cancelled;
    private boolean removeEquipmentOnFail;
    private double chance;
    private double extraLevelChance;
    private HumanEntity player;
    private ItemStack equipment, book, extra;
    
    public HCraftEnchantItemEvent(@Nonnull final HumanEntity player, @Nonnull final ItemStack equipment, 
    		@Nonnull final ItemStack book, final ItemStack extra) {
    	this.removeEquipmentOnFail = true;
    	this.chance = 0d;
    	this.player = player;
    	this.equipment = equipment;
    	this.book = book;
    	this.extra = extra;
    	this.extraLevelChance = 0d;
    	
    	if (!check(player, equipment, book, extra)) {
    		this.cancelled = true;
    		return;
    	}
    	
    	init();
    }

    // ---- Getters/Setters ---- //
    
    /**
	 * @return the chance
	 */
	public double getChance() {
		return chance;
	}

	/**
	 * @param chance the chance
	 */
	public void setChance(double chance) {
		this.chance = chance;
	}

	/**
	 * @return player who using SuperEnchantmentTable.
	 */
	public HumanEntity getPlayer() {
		return player;
	}

	/**
	 * @return the equipment
	 */
	public ItemStack getEquipment() {
		return equipment;
	}

	/**
	 * @return the enchanted book
	 */
	public ItemStack getBook() {
		return book;
	}

	/**
	 * @return the extra
	 */
	public ItemStack getExtraItem() {
		return extra;
	}

	public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    /**
	 * @return the removeEquipmentOnFail
	 */
	public boolean isRemoveEquipmentOnFail() {
		return removeEquipmentOnFail;
	}

	/**
	 * @param removeEquipmentOnFail equipment will disappear on fail if true.
	 */
	public void setRemoveEquipmentOnFail(boolean removeEquipmentOnFail) {
		this.removeEquipmentOnFail = removeEquipmentOnFail;
	}

	/**
	 * @return the extraLevelChance
	 */
	public double getExtraLevelChance() {
		return extraLevelChance;
	}

	/**
	 * @param extraLevelChance the extraLevelChance to set
	 */
	public void setExtraLevelChance(double extraLevelChance) {
		this.extraLevelChance = extraLevelChance;
	}

	public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    // --- Functional Methods --- //
    
    private void init() {
    	//get chance from enchantedbook.
		AdvancedEnchantedBook enchantedBook = new AdvancedEnchantedBook();
		enchantedBook.parse(book);
		this.setChance(enchantedBook.getChance());
		// Extra item
		if (extra == null) return;
		if (new Amulet().isParsable(extra)) {
			this.removeEquipmentOnFail = false;
			return;
		}
		if (new LuckyDust().isParsable(extra)) {
			LuckyDust dust = new LuckyDust();
			dust.parse(extra);
			this.chance += dust.getChance();
			return;
		}
		if (new MagicGem().isParsable(extra)) {
			MagicGem gem = new MagicGem();
			gem.parse(extra);
			this.extraLevelChance += gem.getChance();
			return;
		}
		
		//
    }
    
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
	
	/** Add custom enchantment from book to item.
	 * 
	 */
	public void enchant() {
		if (this.isCancelled()) return;
		
		try {
			double chance = this.getChance();
			double extraLevelChance = this.getExtraLevelChance();
			ItemStack item = this.getEquipment();
			ItemStack book = this.getBook();
			HumanEntity p = this.getPlayer();
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
				p.getInventory().addItem(this.isRemoveEquipmentOnFail() ? new ItemStack(Material.AIR) : item);
				p.sendMessage("§c§l(!) §r§cPhù phép thất bại.");
			}
			
		} catch (Exception ee) {
			this.getPlayer().sendMessage("§cError...#HCraftEnchantItemEvent#enchant");
			Bukkit.getServer().getLogger().info("§cError...#HCraftEnchantItemEvent#enchant");
		}
	}

}
