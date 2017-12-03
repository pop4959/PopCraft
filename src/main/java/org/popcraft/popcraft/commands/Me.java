package org.popcraft.popcraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.utils.Message;

import java.io.File;

@Deprecated
public class Me implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("me")) {
            String player1 = sender.getName();
            File file = new File("plugins/Essentials/userdata/" + player.getUniqueId() + ".yml");
            YamlConfiguration userdata = YamlConfiguration.loadConfiguration(file);
            if (!userdata.contains("muted") || (userdata.contains("muted") && !userdata.getBoolean("muted"))) {
                String message = "";
                int a = 0;
                if (args.length > 0) {
                    while (a < args.length) {
                        message = (message + args[a] + " ");
                        a++;
                    }
                    Bukkit.broadcastMessage("* " + player1 + " " + message);
                } else {
                    Message.usage(player, "me <action>");
                }
            } else {
                Message.normal(player, "Your voice has been silenced!");
            }
            return true;
        }
        return false;
    }
}