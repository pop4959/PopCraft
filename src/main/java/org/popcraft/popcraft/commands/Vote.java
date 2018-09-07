package org.popcraft.popcraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.utils.Message;

import java.util.Collections;
import java.util.List;

public class Vote implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	Player player = (Player) sender;
	if (cmd.getName().equalsIgnoreCase("vote")) {
	    Message.normal(player,
		    "You can vote once per day at the following links:\n" + ChatColor.GREEN
			    + "http://goo.gl/uJtnjS \nhttp://goo.gl/6xzd1f \nhttp://goo.gl/m7pagK" + ChatColor.GOLD
			    + "\nThank you for supporting PopCraft!");
	    return true;
	}
	return false;
    }

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return Collections.emptyList();
	}
}
