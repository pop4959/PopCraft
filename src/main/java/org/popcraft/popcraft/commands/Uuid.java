package org.popcraft.popcraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.utils.Message;

public class Uuid implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	Player player = (Player) sender;
	if (cmd.getName().equalsIgnoreCase("uuid")) {
	    if (args.length == 1) {
		try {
		    Message.normal(player,
			    args[0] + "'s UUID: " + ChatColor.RED + Bukkit.getPlayer(args[0]).getUniqueId().toString());
		} catch (NullPointerException e) {
		    Message.error(player, "That player does not exist!");
		}
	    } else {
		Message.usage(player, "uuid <username>");
	    }
	    return true;
	}
	return false;
    }
}