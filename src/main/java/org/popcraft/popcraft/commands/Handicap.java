package org.popcraft.popcraft.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.utils.Message;

public class Handicap implements CommandExecutor {

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
                    if (hp >= 0 && hp <= 20 || player.hasPermission("popcraft.handicap.bypass")) {
                        if (hp == 0)
                            player.setHealthScale(0.001);
                        else if (player.hasPermission("popcraft.handicap.bypass")) {
                            player.setHealthScale(hp);
                        } else if (hp <= 20) {
                            player.setHealthScale(hp);
                        }
                        Message.normal(player, "Handicap set to " + ChatColor.RED + hp * 5 + ChatColor.GOLD + "%");
                    } else
                        Message.error(player, "You must enter a value between 0 and 20.");
                } else
                    Message.usage(player, "handicap <hp/off>");
                return true;
            } catch (NumberFormatException e) {
                Message.error(player, "You must enter a value between 0 and 20.");
            }
        }
        return false;
    }
}