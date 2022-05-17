package me.dakto101.api;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Entity;

public class Toggle {
	
	private static final Map<UUID, Boolean> ACTIVE_TOGGLE = new HashMap<UUID, Boolean>();

    /**
     * Set toggle for toggle type.
     *	
     * @param uuid entity uuid to store as a hashmap key.
     * @param on true or false
     * @param type enum toggle type
     */
	public static void setToggle(@Nonnull UUID uuid, @Nonnull boolean on, @Nonnull ToggleType type) {
		Validate.notNull(uuid, "UUID can't be null!");
		Validate.notNull(type, "Toggle type can not be null!!");
		if (ACTIVE_TOGGLE.size() > 100) ACTIVE_TOGGLE.clear();
		switch (type) {
		case ACTIVE_SKILL:
			ACTIVE_TOGGLE.putIfAbsent(uuid, on);
			ACTIVE_TOGGLE.replace(uuid, on);
			break;
		default:
			break;
		}
	}
    /**
     * Get player the toggle
     *	
     * @param uuid entity uuid to get the toggle type data.
     * @param type enum toggle type
     * @return player toggle type data
     */
	public static boolean getToggle(UUID uuid, ToggleType type) {
		Validate.notNull(uuid, "UUID can't be null!");
		Validate.notNull(type, "Toggle type can not be null!!");
		boolean result = false;
		switch (type) {
		case ACTIVE_SKILL:
			result = (ACTIVE_TOGGLE.get(uuid) == null) ? false : ACTIVE_TOGGLE.get(uuid);
			break;
		default:
			break;
		}
		return result;
	}
	
    /**
     * Send toggle message.
     *	
     * @param entity entity to send message.
     * @param type enchantment name.
     * @param type enum cooldown type.
     */
	public static void sendMessage(Entity entity, ToggleType type) {
		String toggle = getToggle(entity.getUniqueId(), type) ? "bật" : "tắt";
		entity.sendMessage("§aBạn đang §e" + toggle + " §a" + type.getName().toLowerCase() + ".");
	}
	
	public static void clearToggleData() {
		ACTIVE_TOGGLE.clear();
	}
	
    /**
     * An enum set cooldown for a specific toggle.
     */
    public enum ToggleType {

        /**
         * For activable skill.
         */
        ACTIVE_SKILL("Kỹ năng kích hoạt"),
        ;
    	private String name;

        /**
         * An enum set type for a specific toggle.
         */
    	ToggleType(final String name) {
    		this.name = name;
    	}

    	public String getName() {
    		return name;
    	}
    }
	
	
	
}
