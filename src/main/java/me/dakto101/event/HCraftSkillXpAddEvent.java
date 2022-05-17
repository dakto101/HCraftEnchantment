package me.dakto101.event;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.dakto101.api.Skill;

public class HCraftSkillXpAddEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    
    private Skill skill;
    private int xpAdd;
    private Player player;
    
    public HCraftSkillXpAddEvent(final int xpAdd, final Skill skill, final Player player) {
    	Validate.notNull(skill, "Skill can't be null.");
    	Validate.notNull(player, "Player can't be null.");
    	
    	this.skill = skill;
    	this.xpAdd = xpAdd;
    	this.player = player;
    	
    }

    // ---- Getters/Setters ---- //

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
	 * @return the xpAdd
	 */
	public int getXpAdd() {
		return xpAdd;
	}

	/**
	 * @param xpAdd the xpAdd to set
	 */
	public void setXpAdd(int xpAdd) {
		this.xpAdd = xpAdd;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    // --- Functional Methods --- //


}
