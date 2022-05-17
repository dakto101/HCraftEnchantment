package me.dakto101.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.bukkit.ChatColor;

import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentAPI;

public class LoreReader {

    /**
     * Parses an enchantment name from a bit of text, assuming the format
     * is "{color}{enchantment} {level}"
     *
     * @param text text to parse from
     * @return enchantment name
     */
    public static String parseEnchantmentName(final String text) {
        Objects.requireNonNull(text, "Text cannot be null");
        if (!text.startsWith(ChatColor.GRAY.toString())) return "";

        final String noColor = text.substring(2);
        final int index = noColor.lastIndexOf(' ');
        final int level = RomanNumerals.fromNumerals(noColor.substring(index + 1));
        return level > 0 ? noColor.substring(0, index)
                : noColor;
    }

    /**
     * Parses a level from a lore line. This expects valid roman numerals to be at the end of the line
     * with no spaces after it, matching the format for enchantments.
     *
     * @param text text to parse the level from
     * @return parsed level or 1 if not found
     */
    public static int parseEnchantmentLevel(final String text) {
        Objects.requireNonNull(text, "Text cannot be null");

        final int index = text.lastIndexOf(' ');
        final int level = RomanNumerals.fromNumerals(text.substring(index + 1));
        return level > 0 ? level : 1;
    }

    /**
     * Formats an enchantment name for appending to an item's lore.
     *
     * @param customEnchantment enchantment name
     * @param level level of the enchantment
     * @return lore string for the enchantment
     */
    public static String formatEnchantment(final CustomEnchantment customEnchantment, final int level) {
        return ChatColor.GRAY + customEnchantment.getName() + " " + RomanNumerals.toNumerals(level);
    }

    /**
     * Checks whether or not the lore line is the line for the given enchantment
     *
     * @param line line to check
     * @return true if the line matches, false otherwise
     */
    public static boolean isEnchantment(final String line) {
    	return CustomEnchantmentAPI.isRegistered(parseEnchantmentName(line));
    }
    
    /**
     * Format a description to list.
     *
     * @param  description enchantment description
     * @param  wordsPerLine sd
     * @return list of split description
     */
    public static List<String> formatDescription(final String description, final int wordsPerLine) {
    	final List<String> result = new ArrayList<String>();
    	List<String> split = Arrays.asList(description.split(" "));
    	for (int i = 0; i < split.size() / 10 + 1; i++) {
    		result.add("");
    	}
    	for (int i = 0; i < split.size(); i++) {
    		String resultLine = "";
    		resultLine += result.get(i / wordsPerLine);
    		resultLine += (split.get(i) + " ");
    		result.set(i / wordsPerLine, resultLine);
    	}
    	//Get last color code and add.
    	for (int i = 1; i < result.size(); i++) {
    		int colorCodePosition = -1;
    		String check = result.get(i - 1);
    		for (int ii = 0; ii < check.length(); ii ++) {
    			if (check.charAt(ii) == '§') colorCodePosition = ii;
    		}
    		if (colorCodePosition >= 0) {
    			String colorCode = check.substring(colorCodePosition, colorCodePosition + 2);
    			result.set(i, colorCode + result.get(i));
    		}
    	}
    	return result;
    }
    
}