package me.dakto101.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.dakto101.database.PlayerClassYAML;

public class SkillAPI {
	
	public static final Map<SkillEnum, Skill> SKILLS = new HashMap<SkillEnum, Skill>();

	public static void registerEnchantments(Skill... skills) {
		for (Skill skill : skills) {
			if (!SkillAPI.SKILLS.containsKey(skill.getSkillEnum())) SkillAPI.SKILLS.put(skill.getSkillEnum(), skill);
			else {
				Bukkit.getConsoleSender().sendMessage("§c[HCraftEnchantment - Skill] Skill " + skill.getName() + " co id = " 
						+ skill.getId() + "bi trung trong he thong.");
				Bukkit.getLogger().info("§c[HCraftEnchantment - Skill] Skill " + skill.getName() + " co id = " 
						+ skill.getId() + "bi trung trong he thong.");
			}
		}
	}
	
    /**
     * @param name skill name
     * @return true if the skill is registered successfully, false otherwise
     */
    public static boolean isRegistered(final String name) {
    	for (SkillEnum i : SkillAPI.SKILLS.keySet()) {
    		Skill s = SkillAPI.SKILLS.get(i);
    		if (s.getName().equals(name)) return true;
    	}
        return false;
    }
    /**
     * @param skillEnum skill name
     * @return true if the skill is registered successfully, false otherwise
     */
    public static boolean isRegistered(final SkillEnum skillEnum) {
        return SkillAPI.SKILLS.containsKey(skillEnum);
    }

    /**
     * @param name name of the skill (not case-sensitive)
     * @return skill with the provided name
     */
    public static Skill getSkill(final String name) {
    	for (SkillEnum i : SkillAPI.SKILLS.keySet()) {
    		Skill s = SkillAPI.SKILLS.get(i);
    		if (s.getName().equals(name)) return s;
    	}
        return null;
    }
    
    /**
     * @param id id of the skill
     * @return skill with the provided name
     */
    public static Skill getSkill(final int id) {
    	for (SkillEnum i : SkillAPI.SKILLS.keySet()) {
    		Skill s = SkillAPI.SKILLS.get(i);
    		if (s.getId() == id) return s;
    	}
        return null;
    }
    
    /**
     * @param skillEnum enum of the skill
     * @return skill with the provided name
     */
    public static Skill getSkill(final SkillEnum skillEnum) {
        return SkillAPI.SKILLS.get(skillEnum);
    }
    
    /**
     * @return map of skills
     */
    public static Map<SkillEnum, Skill> getSkills() {
        return SkillAPI.SKILLS;
    }
    
    /**
     * @param player to grab the playerSkill from
     * @return player skill
     */
    public static List<Skill> getPlayerSkills(final Player player) {
        List<Skill> playerSkills = new ArrayList<Skill>();
        if (!PlayerClassYAML.isExists(player)) return playerSkills;
        PlayerSkill ps = new PlayerSkill(player);
        ps.load(player);
        for (Integer i : ps.getPlayerSkills().keySet()) {
        	playerSkills.add(ps.getPlayerSkills().get(i));
        }
    	return playerSkills;
    }
}
