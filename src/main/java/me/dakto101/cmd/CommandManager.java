package me.dakto101.cmd;

import java.util.Arrays;

import org.bukkit.command.PluginCommand;

import me.dakto101.HCraftEnchantment;
import me.dakto101.permission.HPermission;

public class CommandManager {

	public static void registerCommand() {
		PluginCommand cmdMain;
		cmdMain = HCraftEnchantment.plugin.getCommand("he");
		cmdMain.setAliases(Arrays.asList("hcraftenchantment"));
		
		cmdMain.setDescription("§fLệnh chính của HCraftEnchantment.");
		cmdMain.setPermission(HPermission.COMMAND_ADMIN.toString());
		cmdMain.setPermissionMessage("§cBạn không có quyền để dùng lệnh này. §e" + cmdMain.getPermission());
		cmdMain.setExecutor(new CmdAdmin());
		
		PluginCommand cmdMember;
		cmdMember = HCraftEnchantment.plugin.getCommand("hce");
		cmdMember.setAliases(Arrays.asList("hcraftenchantmentmember", "phuphep"));
		cmdMember.setDescription("§fLệnh chính của HCraftEnchantmentMember.");
		cmdMember.setPermission(HPermission.COMMAND_MEMBER.toString());
		cmdMember.setPermissionMessage("§cBạn không có quyền để dùng lệnh này. §e" + cmdMember.getPermission());
		cmdMember.setExecutor(new CmdMember());
	}

}
