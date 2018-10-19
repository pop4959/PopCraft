package org.popcraft.popcraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.utils.Message;

import java.util.Collections;
import java.util.List;

public class Staff implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("staff")) {
            String message = "";
            int a = 0;
            if (args.length > 0) {
                while (a < args.length) {
                    message = message + args[a] + " ";
                    a++;
                }
                message = ChatColor.GREEN + "" + ChatColor.BOLD + "Staff " + ChatColor.RESET
                        + ((Player) sender).getDisplayName() + ChatColor.RESET + ": " + message;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.hasPermission("popcraft.staff")) {
                        Message.normal(p, ChatColor.translateAlternateColorCodes('&', message));
                    }
                }
            } else {
                Message.usage(player, "staff <message>");
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return Collections.emptyList();
    }
}
