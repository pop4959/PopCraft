package org.popcraft.popcraft.commands;

import com.google.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.popcraft.popcraft.PopCommand;

@PopCommand("worldspawn")
public class WorldSpawn implements CommandExecutor {

    private final Server server;
    private final FileConfiguration config;

    @Inject
    public WorldSpawn(final Server server, final FileConfiguration config) {
        this.server = server;
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        this.server.getWorld("world")
                .setSpawnLocation(
                        (int) this.config.getDouble("spawn.coordinate-x"),
                        (int) this.config.getDouble("spawn.coordinate-y"),
                        (int) this.config.getDouble("spawn.coordinate-z")
                );
        return true;
    }

}
