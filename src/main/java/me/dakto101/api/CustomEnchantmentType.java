package me.dakto101.api;

import java.util.ArrayList;
import java.util.List;

public enum CustomEnchantmentType {

    /**
     * For potion enchantment.
     * Ex: Độc, Khô héo, Tốc độ...
     */
	POTION("Thuốc"), 
    /**
     * For melee enchantment.
     * Ex: Chí mạng, hút máu...
     */
	MELEE("Cận chiến"), 
    /**
     * For ranged enchantment.
     * Ex: Chuan xac, cu nhay ten lua...
     */
	RANGED("Đánh xa"),
    /**
     * For defense enchantment.
     * Ex: Bao hiem, ben bi...
     */
	DEFENSE("Giáp"),
	
	ULTILITY("Đa dụng"),
	
	OTHER("Khác");

	private String name;

	CustomEnchantmentType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	/**
	 * 
	 * @param list of CustomenchantmentType
	 * @return CustomEnchantmentType toString
	 */
	public static String toString(List<CustomEnchantmentType> list) {
		String s = "";
		for (CustomEnchantmentType type : list) {
			s += type.getName();
			s += ", ";
		}
		s = s.substring(0, s.length() - 2);
		return s;
	}
	/**
	 * 
	 * @param s string formatted by toString(List<CustomEnchantmentType>)
	 * @see CustomEnchantmentType#toString(List)
	 * @return list of CustomEnchantmentType
	 */
	public static List<CustomEnchantmentType> toList(String s) {
		List<CustomEnchantmentType> result = new ArrayList<CustomEnchantmentType>();
		for (CustomEnchantmentType type : CustomEnchantmentType.values()) {
			if (s.contains(type.getName())) result.add(type);
		}
		return result;
	}
}
