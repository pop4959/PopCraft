package org.popcraft.popcraft.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.popcraft.popcraft.utils.Message;

@Deprecated
public class Lockdown implements CommandExecutor {

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
}