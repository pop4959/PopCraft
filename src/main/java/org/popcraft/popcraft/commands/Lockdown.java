package org.popcraft.popcraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.popcraft.popcraft.utils.Message;
import net.md_5.bungee.api.ChatColor;

import java.util.Collections;
import java.util.List;

public class Lockdown implements CommandExecutor, TabCompleter {

    static boolean lockdown = false;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	if (cmd.getName().equalsIgnoreCase("lockdown")) {
	    lockdown = !lockdown;
	    if (lockdown)
		Message.normal(sender, "Lockdown " + ChatColor.RED + "enabled" + ChatColor.GOLD + ".");
	    else
		Message.normal(sender, "Lockdown " + ChatColor.RED + "disabled" + ChatColor.GOLD + ".");
	    return true;
	}
	return false;
    }

    public static boolean isLockdown() {
	return lockdown;
    }

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return Collections.emptyList();
	}
}
