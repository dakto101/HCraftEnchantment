package me.dakto101.api;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Entity;

public class Cooldown {

    private static final Map<UUID, Long> ACTIVE_TIMER = new HashMap<UUID, Long>();
    private static final Map<UUID, Long> PASSIVE_TIMER = new HashMap<UUID, Long>();
    private static final Map<UUID, Long> ARMOR_TIMER = new HashMap<UUID, Long>();
    private static final Map<UUID, Long> BOW_TIMER = new HashMap<UUID, Long>();
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
        if (ACTIVE_TIMER.size() > 100) ACTIVE_TIMER.clear();
        if (PASSIVE_TIMER.size() > 100) PASSIVE_TIMER.clear();
        if (ARMOR_TIMER.size() > 100) ARMOR_TIMER.clear();
        if (BOW_TIMER.size() > 100) ARMOR_TIMER.clear();
        long time = (long) (System.currentTimeMillis() + second * 1000 + 1000);
        switch (type) {
            case ACTIVE:
                ACTIVE_TIMER.putIfAbsent(uuid, time);
                ACTIVE_TIMER.replace(uuid, time);
                break;
            case ARMOR:
                ARMOR_TIMER.putIfAbsent(uuid, time);
                ARMOR_TIMER.replace(uuid, time);
                break;
            case PASSIVE:
                PASSIVE_TIMER.putIfAbsent(uuid, time);
                PASSIVE_TIMER.replace(uuid, time);
                break;
            case BOW:
                BOW_TIMER.putIfAbsent(uuid, time);
                BOW_TIMER.replace(uuid, time);
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
            case ACTIVE:
                result = (ACTIVE_TIMER.get(uuid) == null) ? 0
                        : (ACTIVE_TIMER.get(uuid) - now) / 1000;
                break;
            case ARMOR:
                result = (ARMOR_TIMER.get(uuid) == null) ? 0
                        : (ARMOR_TIMER.get(uuid) - now) / 1000;
                break;
            case PASSIVE:
                result = (PASSIVE_TIMER.get(uuid) == null) ? 0
                        : (PASSIVE_TIMER.get(uuid) - now) / 1000;
                break;
            case BOW:
                result = (BOW_TIMER.get(uuid) == null) ? 0
                        : (BOW_TIMER.get(uuid) - now) / 1000;
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
        ACTIVE_TIMER.clear();
        PASSIVE_TIMER.clear();
        BOW_TIMER.clear();
        ARMOR_TIMER.clear();
    }

    /**
     * An enum set cooldown for a specific enchantment.
     */
    public enum CooldownType {

        /**
         * For activable enchantment.
         */
        ACTIVE,
        /**
         * For passive enchantment.
         */
        PASSIVE,
        /**
         * For armor enchantment.
         */
        ARMOR,
        /**
         * For bow enchantment.
         */
        BOW,
    }


}
