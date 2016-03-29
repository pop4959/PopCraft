package org.popcraft.popcraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.popcraft.popcraft.utils.MCAPI;
import org.popcraft.popcraft.utils.Message;

public class History implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	if (cmd.getName().equalsIgnoreCase("history")) {
	    if (args.length == 1) {
		String[] history = MCAPI.getHistory(args[0]);
		if (history != null)
		{
		    StringBuilder names = new StringBuilder();
		    for(String name : history)
			names.append(name + ", ");
		    names.delete(names.length()-2, names.length());
		    Message.normal(sender,
			    args[0] + "'s names: " + ChatColor.RED + names.toString());
		}
		else
		    Message.error(sender, "Cannot get name history for that user.");
	    } else {
		Message.usage(sender, "history <username>");
	    }
	    return true;
	}
	return false;
    }
}