package org.popcraft.popcraft.commands;

import com.google.common.base.Joiner;
import com.google.inject.Inject;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.newCode.PopCommand;
import org.popcraft.popcraft.utils.Message;

import java.io.File;

@PopCommand("me")
public class Me implements CommandExecutor {

    private final Server server;

    @Inject
    public Me(Server server) {
        this.server = server;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player) || (args.length < 1))
            return false;
        final Player player = (Player) sender;
        File playerFile = new File("plugins/Essentials/userdata/" + player.getUniqueId() + ".yml");
        YamlConfiguration userdata = YamlConfiguration.loadConfiguration(playerFile);
        if (!(userdata.contains("muted") && userdata.getBoolean("muted")))
            this.server.broadcastMessage("* " + player.getName() + " " + Joiner.on(" ").join(args));
        else
            Message.normal(player, "Your voice has been silenced!");
        return true;
    }
}