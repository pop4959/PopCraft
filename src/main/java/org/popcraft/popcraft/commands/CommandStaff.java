package org.popcraft.popcraft.commands;

import net.milkbowl.vault.chat.Chat;
import org.apache.commons.lang.StringUtils;
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
        String message = ChatColor.translateAlternateColorCodes('&', StringUtils.join(args, ' '));
        String prefix = "";
        String name;
        if (sender instanceof Player) {
            Player player = (Player) sender;
            name = player.getDisplayName();
            Chat chat = plugin.getChat();
            if (chat != null) {
                prefix = chat.getPlayerPrefix(player);
            }
        } else if (sender instanceof ConsoleCommandSender) {
            name = plugin.getMessage("consoleName");
            prefix = plugin.getMessage("consolePrefix");
        } else {
            return Result.UNSUPPORTED_SENDER;
        }
        Bukkit.broadcast(plugin.getMessage("staffChatFormat",
                plugin.getMessage("chatFormat", prefix, name, message)), "popcraft.staff");
        return Result.SUCCESS;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
