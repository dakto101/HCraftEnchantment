package me.dakto101.cmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.dakto101.listener.AdminListener;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.dakto101.api.Cooldown;
import me.dakto101.api.CustomEnchantment;
import me.dakto101.api.CustomEnchantmentAPI;
import me.dakto101.api.CustomEnchantmentType;
import me.dakto101.gui.AdminMenuGUI;
import me.dakto101.gui.EnchantmentListGUI;
import me.dakto101.item.ItemQuality;
import me.dakto101.item.RandomEnchantedBook;
import me.dakto101.item.SkillBook;
import me.dakto101.util.Utils;

public class CmdAdmin implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }

        if (args[0].toString().equals("add") && args.length >= 3) {
            addCommand(sender, args);
            return true;
        }
        if (args[0].toString().equals("remove")) {
            removeCommand(sender, args);
            return true;
        }
        if (args[0].toString().equals("menu")) {
            menuCommand(sender, args);
            return true;
        }
        if (args[0].toString().equals("list")) {
            listCommand(sender, args);
            return true;
        }
        if (args[0].toString().equals("test")) {
            testCommand(sender, args);
            return true;
        }
        if (args[0].toString().equals("clearcd")) {
            clearCooldownCommand(sender, args);
            return true;
        }
        if (args[0].toString().equals("givebook")) {
            giveBookCommand(sender, args);
            return true;
        }
        if (args[0].toString().equals("viewfinaldamage")) {
            viewFinalDamage(sender, args);
            return true;
        }


        sendHelpMessage(sender);
        return true;
    }


    /**
     * Command: he help
     *
     * @param sender
     */
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage("§3----------HCraftEnchantment Admin----------");
        sender.sendMessage("§3/he add <enchantment> <cấp>: Thêm phù phép vào vật phẩm đang cầm trên tay.");
        sender.sendMessage("§3/he remove <enchantment/all>: Xóa một/tất cả phù phép khỏi vật phẩm đang cầm trên tay.");
        sender.sendMessage("§3/he menu: Xem menu cho admin.");
        sender.sendMessage("§3/he list: Xem danh sách phù phép");
        sender.sendMessage("§3/he test: Test...");
        sender.sendMessage("§3/he clearcd: Clear dữ liệu cooldown.");
        sender.sendMessage("§3/he givebook randomenchantedbook <quality> <player>: Gửi sách cho người chơi");
        sender.sendMessage("§3/he viewfinaldamage: Bật chế độ xem final damage trong bán kính 5 ô.");
        sender.sendMessage("§3/hce: Lệnh cho member.");
    }

    /**
     * Command: he add
     *
     * @param sender
     * @param args
     */
    private void addCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cPhải là người chơi mới dùng được lệnh này.");
            return;
        }
        if (args.length < 3) return;

        String name = "", slevel = "";
        for (int i = 1; i < args.length; i++) {
            if (i == args.length - 1) {
                slevel = args[i].toString();
            } else {
                name += args[i].toString();
                name += (i == args.length - 2 ? "" : " ");
            }
        }
        int level = 0;
        try {
            level = Integer.parseInt(slevel);
        } catch (final Exception e) {
            sender.sendMessage("§cCấp độ phù phép phải là con số.");
            return;
        }
        if (!CustomEnchantmentAPI.isRegistered(name)) {
            sender.sendMessage("§cPhù phép này không tồn tại.");
            return;
        }
        Player p = (Player) sender;
        CustomEnchantment ce = CustomEnchantmentAPI.getEnchantment(name);
        if (ce != null) {
            ItemStack item = p.getInventory().getItemInMainHand();
            if (Utils.isPresent(item)) {
                item = ce.addToItem(item, level);
                p.getInventory().setItemInMainHand(item);
                p.sendMessage("§aThêm phù phép thành công.");
            } else {
                p.sendMessage("§cBạn phải cầm vật phẩm cần phù phép trên tay!");
            }
        }
    }

    /**
     * Remove an enchantment or all enchantment from item.
     */
    private void removeCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cPhải là người chơi mới dùng được lệnh này.");
            return;
        }
        Player p = (Player) sender;
        ItemStack item = p.getInventory().getItemInMainHand();
        String name = "";
        for (int i = 1; i < args.length; i++) {
            name += args[i].toString();
            name += (i == args.length - 1 ? "" : " ");
        }
        if (Utils.isPresent(item)) {
            if ((args.length == 2) && (args[1].equals("all"))) {
                item = CustomEnchantmentAPI.removeAllEnchantments(item);
            } else item = CustomEnchantmentAPI.removeCustomEnchantment(item, name);
            p.getInventory().setItemInMainHand(item);
            p.sendMessage("§aXoá phù phép thành công.");
        } else {
            p.sendMessage("§cBạn phải cầm vật phẩm cần phù phép trên tay!");
        }
        p.getInventory().setItemInMainHand(item);
    }

    /**
     * Command: he test
     */
    private void testCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            SkillBook book = new SkillBook();
            ((Player) sender).getInventory().addItem(book.createItem());
        }
        ;
    }

    /**
     * Command: he menu
     */
    private void menuCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            ((Player) sender).openInventory(AdminMenuGUI.adminMenu);
            ((Player) sender).getWorld().playSound(((Player) sender).getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1, 1);
        }
        ;
    }

    /**
     * Command: he list
     */
    private void listCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            ((Player) sender).playSound(((Player) sender).getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1, 1);
            ((Player) sender).openInventory(EnchantmentListGUI.enchantmentType);
        }
    }

    /**
     * Command: he clearcd
     */
    private void clearCooldownCommand(CommandSender sender, String[] args) {
        Cooldown.clearCooldownData();
    }

    /**
     * Command: he givebook
     */
    private void giveBookCommand(CommandSender sender, String[] args) {
        //if (args.length != 5) return;
        String type = args[1].toString();
        String quality = args[2].toString();
        String playerName = args[3].toString();
        if (type.equals("randomenchantedbook")) {
            RandomEnchantedBook randomBook = new RandomEnchantedBook();
            randomBook.setQuality(ItemQuality.getItemQuality(Integer.parseInt(quality)));

            List<CustomEnchantmentType> typeList = new ArrayList<CustomEnchantmentType>();
            for (CustomEnchantmentType t : CustomEnchantmentType.values()) {
                typeList.add(t);
            }
            if (!typeList.isEmpty()) randomBook.setType(typeList);
            Bukkit.getPlayer(playerName).getInventory().addItem(randomBook.createItem());

        }
    }

    /**
     * Command: he viewfinaldamage
     */
    private void viewFinalDamage(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            if (AdminListener.TOGGLE.contains(((Player) sender).getUniqueId())) {
                AdminListener.TOGGLE.remove(((Player) sender).getUniqueId());
                sender.sendMessage("§aĐã tắt chế độ xem final damage cho admin.");
            } else {
                AdminListener.TOGGLE.add(((Player) sender).getUniqueId());
                sender.sendMessage("§aĐã bật chế độ xem final damage cho admin.");
            }
        }
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("add", "remove", "menu", "list", "test", "clearcd", "givebook", "viewfinaldamage");
        }
        if (args.length == 2) {
            if (args[0].toString().equals("givebook")) {
                return Arrays.asList("randomenchantedbook");
            }
        }
        if (args.length == 3) {
            if (args[0].toString().equals("givebook") && (args[1].toString().equals("randomenchantedbook"))) {
                List<String> list = new ArrayList<String>();
                for (ItemQuality q : ItemQuality.values()) {
                    list.add("" + q.getLevel());
                }
                return list;
            }
        }
        return null;
    }
}

