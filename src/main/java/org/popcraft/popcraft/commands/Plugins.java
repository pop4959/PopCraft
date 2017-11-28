package org.popcraft.popcraft.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Plugins implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("plugins")) {
            sender.sendMessage("Plugins (1): " + ChatColor.GREEN + "PopCraft");
            return true;
        }
        return false;
    }
}