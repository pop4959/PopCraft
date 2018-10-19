package org.popcraft.popcraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.PopCraft;
import org.popcraft.popcraft.utils.Cooldown;
import org.popcraft.popcraft.utils.Message;

import java.util.Collections;
import java.util.List;

public class Tpr implements CommandExecutor, TabCompleter {

    private static final int COOLDOWN = PopCraft.config.getInt("commands.tpr.cooldown"),
            RANGE = PopCraft.config.getInt("commands.tpr.range"),
            EXTENDED_RANGE = PopCraft.config.getInt("commands.tpr.extendedrange");

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        final Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("tpr")) {
            if (Cooldown.check(player, "tpr", COOLDOWN)) {
                Bukkit.getScheduler().runTask(PopCraft.getPlugin(), new Runnable() {
                    public void run() {
                        boolean notSafe = true;
                        Location randLoc = player.getLocation();
                        while (notSafe) {
                            randLoc = player.hasPermission("popcraft.tpr.extended") ? getRandomCoordinate()
                                    : getPseudoRandomCoordinate();
                            if (!randLoc.getBlock().getBiome().equals(Biome.RIVER)
                                    && !randLoc.getBlock().getBiome().equals(Biome.FROZEN_RIVER)
                                    && !randLoc.getBlock().getBiome().equals(Biome.DEEP_OCEAN)
                                    && !randLoc.getBlock().getBiome().equals(Biome.OCEAN)
                                    && !randLoc.getBlock().getBiome().equals(Biome.FROZEN_OCEAN)
                                    && randLoc.add(0, -1, 0).getBlock().getType() != Material.LAVA) {
                                notSafe = false;
                                player.teleport(randLoc);
                                Message.normal(player, "Teleporting to a random location...");
                            }
                        }
                    }
                });
            } else {
                Message.cooldown(player, "tpr", COOLDOWN);
            }
            return true;
        }
        return false;
    }

    private Location getPseudoRandomCoordinate() {
        double[] direction = {Math.random() > 0.5 ? 1 : -1, Math.random() > 0.5 ? 1 : -1};
        Location location = new Location(Bukkit.getServer().getWorlds().get(0), RANGE * direction[0] * Math.random(), 0,
                RANGE * direction[1] * Math.random());
        location.setY(location.getWorld().getHighestBlockYAt(location) + 1);
        return location;
    }

    private Location getRandomCoordinate() {
        double[] direction = {Math.random() > 0.5 ? 1 : -1, Math.random() > 0.5 ? 1 : -1};
        Location location = new Location(Bukkit.getServer().getWorlds().get(0),
                EXTENDED_RANGE * direction[0] * Math.random(), 0, EXTENDED_RANGE * direction[1] * Math.random());
        location.setY(location.getWorld().getHighestBlockYAt(location) + 1);
        return location;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return Collections.emptyList();
    }

}
