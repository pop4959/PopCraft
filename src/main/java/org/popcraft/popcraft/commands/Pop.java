package org.popcraft.popcraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@Deprecated
public class Pop implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("pop")) {
            String msg = "";
            for (String s : args)
                msg += " " + s;
            msg = ChatColor.translateAlternateColorCodes('&', msg);
            Bukkit.broadcastMessage(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Owner" + ChatColor.RESET + " "
                    + ChatColor.DARK_GREEN + "pop4959" + ChatColor.RESET + ":" + msg);
            return true;
        }
        return false;
    }
}