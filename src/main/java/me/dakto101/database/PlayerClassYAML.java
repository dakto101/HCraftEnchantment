package me.dakto101.database;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.Plugin;

import me.dakto101.HCraftEnchantment;

public class PlayerClassYAML {
	public static File file;
	public static File folder;
	private static final String FOLDER_NAME = "player_skill";
	public static FileConfiguration config;
	private static Plugin plugin = HCraftEnchantment.plugin;
	
	public static void setup(UUID uuid) {
		folder = new File(plugin.getDataFolder(), FOLDER_NAME);
		if (!folder.exists()) folder.mkdirs();
		file = new File(plugin.getDataFolder() + "\\" + FOLDER_NAME, uuid + ".yml");
		//if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
		if (!file.exists()) {
			try {
				file.createNewFile();
				load(uuid);
			} catch (IOException e) {
				Bukkit.getServer().getConsoleSender().sendMessage("§cTao file khong thanh cong cho " + Bukkit.getOfflinePlayer(uuid).getName() + ".");
			} finally {
				Bukkit.getServer().getConsoleSender().sendMessage("§aTao file thanh cong cho " + Bukkit.getOfflinePlayer(uuid).getName() + ".");
			}
		}
	}
	
	/** Check player class is exists in databases.
	 *
	 * @param uuid player uuid
	 */
	public static boolean isExists(UUID uuid) {
		return new File(plugin.getDataFolder() + "\\" + FOLDER_NAME, uuid + ".yml").exists();
	}
	
	/** Check player class is exists in databases.
	 * 
	 * @param player
	 */
	public static boolean isExists(HumanEntity player) {
		return new File(plugin.getDataFolder() + "\\" + FOLDER_NAME, player.getUniqueId() + ".yml").exists();
	}
	
	public static void save(UUID uuid) {
	    try {
			config.save(file);
		} catch (IOException e) {
			Bukkit.getServer().getConsoleSender().sendMessage("§cKhong the luu file " + uuid + ".yml");
			e.printStackTrace();
		}
	}
	public static void load(UUID uuid) {
		folder = new File(plugin.getDataFolder(), FOLDER_NAME);
		if (!folder.exists()) folder.mkdirs();
		file = new File(plugin.getDataFolder() + "\\" + FOLDER_NAME, uuid + ".yml");
		config = YamlConfiguration.loadConfiguration(file);
	}

	/** Load, setup and save player UUID.
	 *
	 * @param uuid player uuid
	 */
	public static void reload(UUID uuid) {
		load(uuid);
		setup(uuid);
		save(uuid);
	}

}
