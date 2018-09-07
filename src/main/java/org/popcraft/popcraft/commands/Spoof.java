package org.popcraft.popcraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.utils.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Spoof implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	Player player = (Player) sender;
	if (cmd.getName().equalsIgnoreCase("spoof")) {
	    if (args.length == 1) {
		if (args[0].equalsIgnoreCase("join"))
		    Bukkit.broadcastMessage(ChatColor.GREEN + "\u2714 " + player.getName());
		else if (args[0].equalsIgnoreCase("quit"))
		    Bukkit.broadcastMessage(ChatColor.GREEN + "\u2715 " + player.getName());
		else if (args[0].equalsIgnoreCase("afk"))
		    Bukkit.broadcastMessage(
			    ChatColor.GRAY + "* " + player.getDisplayName() + ChatColor.GRAY + " is now AFK.");
		else if (args[0].equalsIgnoreCase("no-afk"))
		    Bukkit.broadcastMessage(
			    ChatColor.GRAY + "* " + player.getDisplayName() + ChatColor.GRAY + " is no longer AFK.");
		else
		    Message.usage(player, "spoof <join/quit/afk/no-afk>");
	    } else
		Message.usage(player, "spoof <join/quit/afk/no-afk>");
	}
	return true;
    }

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> options = new ArrayList<>();
		if (args.length == 1) {
			options.add("join");
			options.add("quit");
			options.add("afk");
			options.add("no-afk");
		}
		List<String> finalOptions = new ArrayList<>();
		for (String option : options) {
			if (option.contains(args[args.length - 1])) {
				finalOptions.add(option);
			}
		}
		return finalOptions;
	}
}
