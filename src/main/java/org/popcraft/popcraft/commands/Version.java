package org.popcraft.popcraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.PluginDescriptionFile;
import org.popcraft.popcraft.PopCraft;
import net.md_5.bungee.api.ChatColor;

import java.util.Collections;
import java.util.List;

public class Version implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	if (cmd.getName().equalsIgnoreCase("version")) {
	    PluginDescriptionFile p = PopCraft.getPlugin().getDescription();
	    sender.sendMessage((ChatColor.GREEN + p.getName() + " " + ChatColor.RESET + "version " + ChatColor.GREEN
		    + p.getVersion() + "\n" + ChatColor.RESET + p.getDescription() + "\n" + "Website: "
		    + ChatColor.GREEN + p.getWebsite() + ChatColor.RESET + "\n" + "Authors: " + p.getAuthors())
			    .replace("pop4959", ChatColor.GREEN + "pop4959" + ChatColor.RESET).replace("[", "")
			    .replace("]", "")
			    .replace("waffelmonster", ChatColor.GREEN + "waffelmonster" + ChatColor.RESET)
			    .replace("2coolGaming", ChatColor.GREEN + "2coolGaming" + ChatColor.RESET));

	    return true;
	}
	return false;
    }

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return Collections.emptyList();
	}
}
