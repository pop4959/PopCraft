package org.popcraft.popcraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandStaff extends PopCraftCommand {

    public CommandStaff() {
        super("staff");
    }

    @Override
    public Result execute(CommandSender sender, Command command, String label, String[] args) {
        String message = ChatColor.translateAlternateColorCodes('&', String.join(" ", args));
        String prefix = "";
        String name;
        if (sender instanceof Player) {
            name = ((Player) sender).getDisplayName();
        } else if (sender instanceof ConsoleCommandSender) {
            name = plugin.getMessage("consolePrefix") + plugin.getMessage("consoleName");
        } else {
            return Result.UNSUPPORTED_SENDER;
        }
        Bukkit.broadcast(plugin.getMessage("staffChatFormat",
                plugin.getMessage("chatFormat", name, message)), "popcraft.staff");
        return Result.SUCCESS;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
