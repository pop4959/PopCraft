package org.popcraft.popcraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.utils.Message;

import java.util.Collections;
import java.util.List;

public class Textures implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("textures")) {
            if (args.length > 0) {
                if (args[0].equals("1")) {
                    try {
                        player.setResourcePack("http://files.popcraft.org:8080/bin/PopCraft1.zip");
                    } catch (NullPointerException e) {
                        Message.error(player,
                                "Cannot find texture pack. Please contact an administrator for assistance.");
                    }
                } else if (args[0].equals("2")) {
                    try {
                        player.setResourcePack("http://files.popcraft.org:8080/bin/PopCraft2.zip");
                    } catch (NullPointerException e) {
                        Message.error(player,
                                "Cannot find texture pack. Please contact an administrator for assistance.");
                    }
                }
            } else {
                try {
                    player.setResourcePack("http://files.popcraft.org:8080/bin/PopCraft.zip");
                } catch (NullPointerException e) {
                    Message.error(player, "Cannot find texture pack. Please contact an administrator for assistance.");
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return Collections.emptyList();
    }
}
