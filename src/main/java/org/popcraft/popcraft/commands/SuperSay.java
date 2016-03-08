package org.popcraft.popcraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SuperSay implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	if (cmd.getName().equalsIgnoreCase("supersay")) {
	    String msg = "";
	    for (String s : args)
		msg += " " + s;
	    msg = ChatColor.translateAlternateColorCodes('&', msg);
	    Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Server" + ChatColor.RESET + " "
		    + ChatColor.LIGHT_PURPLE + "Console" + ChatColor.RESET + ":" + msg);
	    return true;
	}
	return false;
    }
}