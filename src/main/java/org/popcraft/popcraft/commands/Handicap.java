package org.popcraft.popcraft.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.utils.Message;

import java.util.ArrayList;
import java.util.List;

public class Handicap implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("handicap")) {
            try {
                if (args.length == 0 || args[0].equalsIgnoreCase("off")) {
                    player.setHealthScale(20);
                    Message.normal(player, "Handicap set to " + ChatColor.RED + 100 + ChatColor.GOLD + "%.");
                } else if (args.length == 1) {
                    int hp = Integer.parseInt(args[0]);
                    if (hp >= 1 && hp <= 20 || player.hasPermission("popcraft.handicap.bypass")) {
                        if (player.hasPermission("popcraft.handicap.bypass")) {
                            player.setHealthScale(hp);
                        } else if (hp <= 20) {
                            player.setHealthScale(hp);
                        }
                        Message.normal(player, "Handicap set to " + ChatColor.RED + hp * 5 + ChatColor.GOLD + "%");
                    } else
                        Message.error(player, "You must enter a value between 1 and 20.");
                } else
                    Message.usage(player, "handicap <hp/off>");
                return true;
            } catch (NumberFormatException e) {
                Message.error(player, "You must enter a value between 1 and 20.");
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> options = new ArrayList<>();
        if (args.length == 1) {
            for (int i = 10; i <= 20; ++i) {
                options.add(Integer.toString(i));
            }
            options.add("off");
        }
        List<String> finalOptions = new ArrayList<>();
        for (String option : options) {
            if (option.contains(args[args.length - 1])) {
                finalOptions.add(option);
            }
        }
        return finalOptions;
    }
}
