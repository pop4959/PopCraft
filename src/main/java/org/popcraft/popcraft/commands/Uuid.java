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

public class Uuid implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("uuid")) {
            if (args.length == 1) {
                String uuid = MCAPI.getUUID(args[0]);
                if (uuid != null)
                    Message.normal(sender, args[0] + "'s UUID: " + ChatColor.RED + uuid);
                else
                    Message.error(sender, "Cannot get UUID for that user.");
            } else {
                Message.usage(sender, "uuid <username>");
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
