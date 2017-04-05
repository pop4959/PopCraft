package org.popcraft.popcraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.utils.Message;

public class Discord implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	Player player = (Player) sender;
	if (cmd.getName().equalsIgnoreCase("discord")) {
			Message.normal(player,
					"Click the link to join the discord: " + ChatColor.GREEN + "https://discord.gg/98Cw8Mz");
	    return true;
	}
	return false;
    }
}