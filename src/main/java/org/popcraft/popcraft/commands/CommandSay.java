package org.popcraft.popcraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CommandSay extends PopCraftCommand {

    public CommandSay() {
        super("say");
    }

    @Override
    public Result execute(CommandSender sender, Command command, String label, String[] args) {
        String message = ChatColor.translateAlternateColorCodes('&', String.join(" ", args));
        Bukkit.broadcastMessage(plugin.getMessage("chatFormat",
                plugin.getMessage("consolePrefix") + plugin.getMessage("consoleName"), message));
        return Result.SUCCESS;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

}
