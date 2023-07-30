package me.dakto101.listener;

import static me.dakto101.HCraftEnchantment.plugin;

import org.bukkit.event.Listener;

public class ListenerManager {

	public static void registerEvents(Listener... listener) {
		for (Listener l : listener) {
			plugin.getServer().getPluginManager().registerEvents(l, plugin);
		}
	}

}
