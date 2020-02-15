package org.popcraft.popcraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandDonate extends PopCraftMessageCommand {

    public CommandDonate() {
        super("donate");
    }

    @Override
    public Result execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && sender instanceof ConsoleCommandSender) {
            Player player = Bukkit.getPlayerExact(args[0]);
            if (player != null && player.isOnline()) {
                player.sendMessage(plugin.getMessage("donationThanks"));
            }
        } else {
            sender.sendMessage(plugin.getMessage(name));
        }
        return Result.SUCCESS;
    }

}
