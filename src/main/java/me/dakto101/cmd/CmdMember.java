package me.dakto101.cmd;

import java.util.List;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import me.dakto101.gui.MemberMenuGUI;
import me.dakto101.gui.PlayerSkillGUI;

public class CmdMember implements TabExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (args.length == 0) {
			sendHelpMessage(sender);
			return true;
		}

		if (args[0].toString().equals("test")) {
			testCommand(sender, args);
			return true;
		}
		if (args[0].toString().equals("menu")) {
			menuCommand(sender, args);
			return true;
		}
		if (args[0].toString().equals("skill")) {
			skillCommand(sender, args);
			return true;
		}
		
		
		sendHelpMessage(sender);
		return true;
	}

	private void sendHelpMessage(CommandSender sender) {
		sender.sendMessage("§b----------HCraftEnchantment(Member)----------");
		sender.sendMessage("§b/hce test: Test?");
		sender.sendMessage("§b/hce menu: Mở giao diện phù phép dành cho người chơi.");
		sender.sendMessage("§b/hce skill: Mở giao diện danh sách kỹ năng dành cho người chơi.");
	}

	private void testCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			//Utils.damage((Player) sender, (Player) sender, DamageSourceEnum.valueOf(args[1].toString()), 10);
		};
	}
	
	private void menuCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			((Player) sender).openInventory(MemberMenuGUI.memberMenu);
			((Player) sender).getWorld().playSound(((Player) sender).getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1, 1);
		};
	}
	
	private void skillCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			((Player) sender).openInventory(PlayerSkillGUI.playerSkillGui);
			((Player) sender).getWorld().playSound(((Player) sender).getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1, 1);
		};
	}

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return null;
	}

}

