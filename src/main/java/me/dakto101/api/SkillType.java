package me.dakto101.api;

import java.util.ArrayList;
import java.util.List;

public enum SkillType {
	
    /** 
     * Ex: 
     */
	ARCHERY("Cung thuật"),
    /** 
     * Ex: 
     */
	MAGIC("Pháp thuật"), 
    /** 
     * Ex: 
     */
	SWORDSMANSHIP("Kiếm thuật"),
    /** 
     * Ex: 
     */
	UNARMED("Võ thuật"), 
	OTHER("Khác");
	
	private String name;

    /**
     * An enum set name for a group of skill.
     */
	SkillType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	/**
	 * 
	 * @param list of SkillType
	 * @return SkillType toString
	 */
	public static String toString(final List<SkillType> list) {
		String s = "";
		for (SkillType type : list) {
			s += type.getName();
			s += ", ";
		}
		s = s.substring(0, s.length() - 2);
		return s;
	}
	/**
	 * 
	 * @param s string formatted by #toString(List<SkillType>)
	 * @see SkillType#toString(List)
	 * @return list of SkillType
	 */
	public static List<SkillType> toList(final String s) {
		List<SkillType> result = new ArrayList<SkillType>();
		for (SkillType type : SkillType.values()) {
			if (s.contains(type.getName())) result.add(type);
		}
		return result;
	}
}
