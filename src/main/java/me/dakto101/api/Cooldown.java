package me.dakto101.api;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Entity;

public class Cooldown {

    private static final Map<UUID, Long> ACTIVE_SKILL_TIMER = new HashMap<UUID, Long>();
    private static final Map<UUID, Long> PASSIVE_SKILL_TIMER = new HashMap<UUID, Long>();
    private static final Map<UUID, Long> MELEE_ENCHANTMENT_TIMER = new HashMap<UUID, Long>();
    private static final Map<UUID, Long> ARMOR_ENCHANTMENT_TIMER = new HashMap<UUID, Long>();
    private static final Map<UUID, Long> BOW_ENCHANTMENT_TIMER = new HashMap<UUID, Long>();
    private static short allowSendMessage = 0;

    /**
     * Set cooldown for enchantment.
     *
     * @param uuid   entity uuid to store as a hashmap key.
     * @param second time in second to store as a hashmap value.
     * @param type   enum cooldown type
     */
    public static void setCooldown(UUID uuid, double second, CooldownType type) {
        Validate.notNull(uuid, "UUID can't be null!");
        Validate.notNull(type, "Cooldown type can not be null!!");
        if (ACTIVE_SKILL_TIMER.size() > 100) ACTIVE_SKILL_TIMER.clear();
        if (PASSIVE_SKILL_TIMER.size() > 100) PASSIVE_SKILL_TIMER.clear();
        if (MELEE_ENCHANTMENT_TIMER.size() > 100) MELEE_ENCHANTMENT_TIMER.clear();
        if (ARMOR_ENCHANTMENT_TIMER.size() > 100) ARMOR_ENCHANTMENT_TIMER.clear();
        if (BOW_ENCHANTMENT_TIMER.size() > 100) ARMOR_ENCHANTMENT_TIMER.clear();
        long time = (long) (System.currentTimeMillis() + second * 1000 + 1000);
        switch (type) {
            case ACTIVE_SKILL:
                ACTIVE_SKILL_TIMER.putIfAbsent(uuid, time);
                ACTIVE_SKILL_TIMER.replace(uuid, time);
                break;
            case PASSIVE_SKILL:
                PASSIVE_SKILL_TIMER.putIfAbsent(uuid, time);
                PASSIVE_SKILL_TIMER.replace(uuid, time);
                break;
            case MELEE_ENCHANTMENT:
                MELEE_ENCHANTMENT_TIMER.putIfAbsent(uuid, time);
                MELEE_ENCHANTMENT_TIMER.replace(uuid, time);
                break;
            case ARMOR_ENCHANTMENT:
                ARMOR_ENCHANTMENT_TIMER.putIfAbsent(uuid, time);
                ARMOR_ENCHANTMENT_TIMER.replace(uuid, time);
                break;
            case BOW_ENCHANTMENT:
                BOW_ENCHANTMENT_TIMER.putIfAbsent(uuid, time);
                BOW_ENCHANTMENT_TIMER.replace(uuid, time);
                break;
            default:
                break;
        }
    }

    /**
     * Get the enchantment cooldown remaining.
     *
     * @param uuid entity uuid to get the cooldown remaining.
     * @param type enum cooldown type
     * @return cooldown remaining in second.
     */
    public static double getCooldownRemaining(UUID uuid, CooldownType type) {
        Validate.notNull(uuid, "UUID can't be null!");
        Validate.notNull(type, "Cooldown type can not be null!!");
        double result = 0d;
        long now = System.currentTimeMillis();
        switch (type) {
            case ACTIVE_SKILL:
                result = (ACTIVE_SKILL_TIMER.get(uuid) == null) ? 0
                        : (ACTIVE_SKILL_TIMER.get(uuid) - now) / 1000;
                break;
            case PASSIVE_SKILL:
                result = (PASSIVE_SKILL_TIMER.get(uuid) == null) ? 0
                        : (PASSIVE_SKILL_TIMER.get(uuid) - now) / 1000;
                break;
            case MELEE_ENCHANTMENT:
                result = (MELEE_ENCHANTMENT_TIMER.get(uuid) == null) ? 0
                        : (MELEE_ENCHANTMENT_TIMER.get(uuid) - now) / 1000;
                break;
            case ARMOR_ENCHANTMENT:
                result = (ARMOR_ENCHANTMENT_TIMER.get(uuid) == null) ? 0
                        : (ARMOR_ENCHANTMENT_TIMER.get(uuid) - now) / 1000;
                break;
            case BOW_ENCHANTMENT:
                result = (BOW_ENCHANTMENT_TIMER.get(uuid) == null) ? 0
                        : (BOW_ENCHANTMENT_TIMER.get(uuid) - now) / 1000;
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * Get the enchantment cooldown remaining.
     *
     * @param uuid entity uuid.
     * @param type cooldown type.
     * @return true if entity enchantment is still on cooldown, false otherwise.
     */
    public static boolean onCooldown(UUID uuid, CooldownType type) {
        return (getCooldownRemaining(uuid, type) > 0);
    }

    /**
     * Reduce cooldown for enchantment.
     *
     * @param uuid   entity uuid to store as a hashmap key.
     * @param second time in second to store as a hashmap value.
     * @param type   enum cooldown type
     */
    public static void reduceCooldown(UUID uuid, int second, CooldownType type) {
        setCooldown(uuid, -second, type);
    }

    /**
     * Send cooldown message.
     *
     * @param entity          to send message.
     * @param enchantmentName enchantment name.
     * @param type            enum cooldown type.
     */
    public static void sendMessage(Entity entity, String enchantmentName, CooldownType type) {
        allowSendMessage++;
        if (allowSendMessage == 2) {
            allowSendMessage = 0;
            entity.sendMessage("§6" + enchantmentName + " §7- Hồi chiêu: §6"
                    + (int) getCooldownRemaining(entity.getUniqueId(), type) + "§7 giây.");
        }
    }

    public static void clearCooldownData() {
        ACTIVE_SKILL_TIMER.clear();
        PASSIVE_SKILL_TIMER.clear();
        MELEE_ENCHANTMENT_TIMER.clear();
        BOW_ENCHANTMENT_TIMER.clear();
        ARMOR_ENCHANTMENT_TIMER.clear();
    }

    /**
     * An enum set cooldown for a specific enchantment.
     */
    public enum CooldownType {

        /**
         * For activable skill.
         */
        ACTIVE_SKILL,
        /**
         * For passive skill.
         *
         * Example: Normal attack effect...
         */
        PASSIVE_SKILL,
        /**
         * For melee enchantment.
         *
         */
        MELEE_ENCHANTMENT,
        /**
         * For armor enchantment.
         */
        ARMOR_ENCHANTMENT,
        /**
         * For bow enchantment.
         */
        BOW_ENCHANTMENT,
    }


}
