package org.popcraft.popcraft.commands;

import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.utils.TabCompleteUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;

public class CommandSpoof extends PopCraftCommand {

    public CommandSpoof() {
        super("spoof");
    }

    @Override
    public Result execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return Result.UNSUPPORTED_SENDER;
        }
        Player player = (Player) sender;
        if (args.length != 1) {
            return Result.INCORRECT_USAGE;
        }
        Essentials essentials = plugin.getEssentials();
        if ("join".equalsIgnoreCase(args[0])) {
            Bukkit.broadcastMessage(plugin.getMessage("join", sender.getName()));
        } else if ("quit".equalsIgnoreCase(args[0])) {
            Bukkit.broadcastMessage(plugin.getMessage("quit", sender.getName()));
        } else if ("afk".equalsIgnoreCase(args[0])) {
            if (essentials != null) {
                Bukkit.broadcastMessage(tl("userIsAway", player.getDisplayName()));
            }
        } else if ("unafk".equalsIgnoreCase(args[0])) {
            if (essentials != null) {
                Bukkit.broadcastMessage(tl("userIsNotAway", player.getDisplayName()));
            }
        } else {
            return Result.INCORRECT_USAGE;
        }
        return Result.SUCCESS;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            TabCompleteUtil.startsWithLastArg(Arrays.asList("join", "quit", "afk", "unafk"), args);
        }
        return Collections.emptyList();
    }

}
