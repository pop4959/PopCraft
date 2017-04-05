package org.popcraft.popcraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.utils.Message;

public class TeamSpeak implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	Player player = (Player) sender;
	if (cmd.getName().equalsIgnoreCase("teamspeak")) {
			Message.normal(player,
					"To join the TeamSpeak3 server using the ip here: " + ChatColor.GREEN + "ts3.popcraft.org");
	    return true;
	}
	return false;
    }
}