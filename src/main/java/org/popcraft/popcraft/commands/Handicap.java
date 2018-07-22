package org.popcraft.popcraft.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.PopCommand;
import org.popcraft.popcraft.utils.Message;

@PopCommand("handicap")
public class Handicap implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player))
            return false;
        Player player = (Player) sender;
        int hp;
        try {
            hp = args.length == 0 ? 20 : Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            return false;
        }
        if (hp <= 0 || hp > 20 && !player.hasPermission("popcraft.handicap.bypass")) {
            Message.error(player, "You must enter a value between 1 and 20.");
            return true;
        }
        player.setHealthScale(hp);
        Message.normal(player, "Handicap set to " + ChatColor.RED + 5 * hp + ChatColor.GOLD + "%");
        return true;
    }
}