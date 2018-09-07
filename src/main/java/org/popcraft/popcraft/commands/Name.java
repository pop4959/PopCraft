package org.popcraft.popcraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.popcraft.popcraft.utils.MCAPI;
import org.popcraft.popcraft.utils.Message;

import java.util.Collections;
import java.util.List;

public class Name implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	if (cmd.getName().equalsIgnoreCase("name")) {
	    if (args.length == 1) {
		String name = MCAPI.getName(args[0]);
		if (name != null)
		    Message.normal(sender, args[0] + " is: " + ChatColor.RED + name);
		else
		    Message.error(sender, "Cannot get that user's name.");
	    } else {
		Message.usage(sender, "name <UUID>");
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
