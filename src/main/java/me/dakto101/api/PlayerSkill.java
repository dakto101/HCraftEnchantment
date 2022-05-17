package me.dakto101.api;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.dakto101.database.PlayerClassYAML;
import me.dakto101.event.HCraftSkillXpAddEvent;

public class PlayerSkill {

	/*
	 * Init map with player name and player skill.
	 */
	//public static final Map<String, PlayerSkill> PLAYERS = new HashMap<String, PlayerSkill>();
	public static final int START_I = 1;
	
	private Player player;
	private Skill playerChosenSkill;
	//Map with index and require book.
	private Map<Integer, Integer> requireBook = new HashMap<Integer, Integer>();
	//Map with index and skill.
	private Map<Integer, Skill> playerSkills = new HashMap<Integer, Skill>();
	//Map with index and level.
	private Map<Integer, Integer> playerSkillLevel = new HashMap<Integer, Integer>();
	
	public PlayerSkill(final Player player) {
		Validate.notNull(player, "Notice: Player must not be null.");
		this.player = player;
		
		load(player);
	}

	// ---- Getters/Setters ---- //

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}



	/**
	 * @return the playerChosenSkill
	 */
	public Skill getPlayerChosenSkill() {
		return playerChosenSkill;
	}

	/**
	 * @param playerChosenSkill the playerChosenSkill to set
	 */
	public void setPlayerChosenSkill(Skill playerChosenSkill) {
		this.playerChosenSkill = playerChosenSkill;
	}
	
	/**
	 * @return map of index and player skill
	 */
	public Map<Integer, Skill> getPlayerSkills() {
		return playerSkills;
	}

	/**
	 * @param playerSkills the playerSkills to set
	 */
	public void setPlayerSkills(Map<Integer, Skill> playerSkills) {
		this.playerSkills = playerSkills;
	}
	
	/**
	 * @return the requireBook
	 */
	public Map<Integer, Integer> getRequireBook() {
		return requireBook;
	}

	/**
	 * 
	 * @param playerSkillLevel
	 */
	public void setPlayerSkillLevel(Map<Integer, Integer> playerSkillLevel) {
		this.playerSkillLevel = playerSkillLevel;
	}
	
	/**
	 * 
	 * @return map of player skill level
	 */
	public Map<Integer, Integer> getPlayerSkillLevel() {
		return this.playerSkillLevel;
	}

	/**
	 * @param requireBook the requireBook to set
	 */
	public void setRequireBook(Map<Integer, Integer> requireBook) {
		this.requireBook = requireBook;
	}
	
	/** To get player skill level.
	 * 
	 * @param skill
	 * @return skill level
	 */
	public int getSkillLevel(final Skill skill) {
		for (Integer index : this.getPlayerSkills().keySet()) {
			Skill check = this.getPlayerSkills().get(index);
			if (check.getId() == skill.getId()) return this.getPlayerSkillLevel().get(index);
		}
		return -1;
	}
	
	/** To get player skill require book.
	 * 
	 * @param skill
	 * @return require book
	 */
	public int getRequireBook(final Skill skill) {
		for (Integer index : this.getPlayerSkills().keySet()) {
			Skill check = this.getPlayerSkills().get(index);
			if (check.getId() == skill.getId()) return this.getRequireBook().get(index);
		}
		return -1;
	}

	/** Check if player has no skill.
	 * 
	 */
	public boolean isEmpty() {
		return (this.player == null || this.playerChosenSkill == null || this.playerSkills.isEmpty());
	}
	
	/** Get require book base on level.
	 * 
	 * @param level
	 * @return require book base on level
	 */
	public static int getRequireBook(final int level) {
		return (int) (2 * level + Math.pow(1.3, level));
	}
	
    // --- Functional Methods --- //
	
	public boolean addSkill(Skill skill) {
		if (SkillAPI.getPlayerSkills(this.player).contains(skill)) return false;
		Map<Integer, Skill> playerSkills = this.getPlayerSkills();
		for (int i = START_I; i < SkillAPI.SKILLS.size() + 10; i++) {
			if (playerSkills.get(i) == null) {
				playerSkills.put(i, skill);
				this.playerSkillLevel.put(i, 1);
				this.requireBook.put(i, getRequireBook(1));
				break;
			}
		}
		this.setPlayerSkills(playerSkills);
		this.save(this.getPlayer());
		return true;
	}
	
	/** Add skill xp when player use book.
	 * 
	 * @param xpAdd xp to add to player skill.
	 * @param skill
	 * @param callEvent if set true.
	 */
	public void addSkillXP(int xpAdd, Skill skill, final boolean callEvent) {

		for (Integer index : this.getPlayerSkills().keySet()) {
			if (this.getPlayerSkills().get(index).equals(skill)) {
				int require = this.getRequireBook().get(index);
				if (callEvent) {
					HCraftSkillXpAddEvent event = new HCraftSkillXpAddEvent(xpAdd, skill, this.getPlayer());
					Bukkit.getServer().getPluginManager().callEvent(event);
					xpAdd = event.getXpAdd();
				}
				if (require - xpAdd > 0) require -= xpAdd;
				else {
					this.getPlayerSkillLevel().replace(index, this.getPlayerSkillLevel().get(index) + 1);
					require += PlayerSkill.getRequireBook(this.getPlayerSkillLevel().get(index));
					require -= xpAdd;
				}
				this.getRequireBook().replace(index, require);
				while (this.getRequireBook().get(index) <= 0) addSkillXP(0, skill, false);
			}
		}

		this.save(this.getPlayer());
	}
	
	public void addSkillLevel(int levelAdd, final Skill skill, final boolean callEvent) {
		
	}
	
	/** Load player skill data from PlayerClassYAML.
	 * 
	 * @param p player
	 */
	public void load(final Player p) {
		try {
			PlayerClassYAML.load(p.getUniqueId());
			this.setPlayerChosenSkill(SkillAPI.getSkill(PlayerClassYAML.config.getInt("chosen-skill-id")));
			Map<Integer, Skill> playerSkills = this.getPlayerSkills();
			for (int i = START_I; i < SkillAPI.SKILLS.size() + 10; i++) {
				
				Object skillId = PlayerClassYAML.config.get("player-skills." + i + ".skill-id");
				if (skillId == null) continue;
				
				int skillLevel = PlayerClassYAML.config.getInt("player-skills." + i + ".skill-level");
				Skill skill = SkillAPI.getSkill((int) skillId);
				
				int requireBook = PlayerClassYAML.config.getInt("player-skills." + i + ".require-book");
				
				if (this.playerSkills.containsKey(i)) this.playerSkills.replace(i, skill);
				else this.playerSkills.put(i, skill);
				if (this.playerSkillLevel.containsKey(i)) this.playerSkillLevel.replace(i, skillLevel);
				else this.playerSkillLevel.put(i, skillLevel);
				if (this.requireBook.containsKey(i)) this.requireBook.replace(i, requireBook);
				else this.requireBook.put(i, requireBook);
			}
			
			this.setPlayerSkills(playerSkills);
		} catch (Exception e) {
			Bukkit.getServer().getLogger().info("Error! PlayerSkill.java #load(final Player)");
		}

	}
	
	/** Remove player skill from database with skill id
	 * 
	 * @param id skill id
	 */
	public void removeSkill(final int id) {
		String skillName = "xoa khong thanh cong";
		PlayerClassYAML.load(this.getPlayer().getUniqueId());
		PlayerClassYAML.config.set("chosen-skill-id", this.getPlayerChosenSkill().getId());
		PlayerClassYAML.config.set("chosen-skill-name", this.getPlayerChosenSkill().getName());
		PlayerClassYAML.config.set("last-modify", System.currentTimeMillis());
		for (Integer index : this.playerSkills.keySet()) {
			Skill skill = this.playerSkills.get(index);
			if (skill.getId() == id) {
				PlayerClassYAML.config.set("player-skills." + index, null);
				skillName = skill.getName();
			}
		}
		PlayerClassYAML.save(this.getPlayer().getUniqueId());
		Bukkit.getLogger().info("Xoa skillId = " + id + "(" + skillName + ") cua nguoi choi " + this.getPlayer().getName());
	}
	
	/** Load player skill data from PlayerClassYAML.
	 * 
	 * @param p player
	 */
	public void save(final Player p) {
		
		if (this.getPlayerSkills().isEmpty()) return;
		if (!PlayerClassYAML.isExists(p)) PlayerClassYAML.setup(p.getUniqueId());
		PlayerClassYAML.load(p.getUniqueId());
		PlayerClassYAML.config.set("chosen-skill-id", this.getPlayerChosenSkill() != null ? this.getPlayerChosenSkill().getId() : null);
		PlayerClassYAML.config.set("chosen-skill-name", this.getPlayerChosenSkill() != null ? this.getPlayerChosenSkill().getName() : null);
		PlayerClassYAML.config.set("last-modify", System.currentTimeMillis());

		for (Integer index : this.getPlayerSkillLevel().keySet()) {
			PlayerClassYAML.config.set("player-skills." + index + ".skill-id", this.getPlayerSkills().get(index).getId());
			PlayerClassYAML.config.set("player-skills." + index + ".skill-name", this.getPlayerSkills().get(index).getName());
			PlayerClassYAML.config.set("player-skills." + index + ".skill-level", this.getPlayerSkillLevel().get(index));
			PlayerClassYAML.config.set("player-skills." + index + ".require-book", this.getRequireBook().get(index));
		}
		PlayerClassYAML.save(p.getUniqueId());
		
	}
	// ---- Object operations ---- //
	
    @Override
    public String toString() {
        return "[name = " + this.player.getName() + ", playerChosenSkill = " + (this.playerChosenSkill == null)
        + ", playerSkillLevel = " + this.playerSkillLevel + ", playerSkills = " + this.playerSkills + "]";
    }
}
