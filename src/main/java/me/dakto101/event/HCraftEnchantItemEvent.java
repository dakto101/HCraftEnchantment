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
    



}
