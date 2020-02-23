package org.popcraft.popcraft.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CommandPop extends PopCraftCommand {

    public CommandPop() {
        super("pop");
    }

    @Override
    public Result execute(CommandSender sender, Command command, String label, String[] args) {
        String message = ChatColor.translateAlternateColorCodes('&', StringUtils.join(args, ' '));
        Bukkit.broadcastMessage(plugin.getMessage("chatFormat", plugin.getMessage("popDisplayName"), message));
        return Result.SUCCESS;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

}
