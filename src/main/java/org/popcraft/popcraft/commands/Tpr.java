package org.popcraft.popcraft.commands;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.PopCraft;
import org.popcraft.popcraft.newCode.PopCommand;
import org.popcraft.popcraft.utils.Message;

import java.util.Set;

import static org.bukkit.Material.*;
import static org.bukkit.block.Biome.*;
import static org.popcraft.popcraft.utils.Cooldown.check;

@PopCommand("tpr")
public class Tpr implements CommandExecutor {

    private static final String EXTENDED_TPR_PERMISSION = "popcraft.tpr.extended";

    private static final Set<Biome> UNSAFE_BIOMES = Sets.immutableEnumSet(
            RIVER,
            FROZEN_RIVER,
            DEEP_OCEAN,
            OCEAN,
            FROZEN_OCEAN
    );

    private static final Set<Material> UNSAFE_MATERIALS = Sets.immutableEnumSet(
            STATIONARY_LAVA,
            LAVA,
            CACTUS
    );

    private final int cooldown;
    private final int range;
    private final int extendedRange;

    @Inject
    public Tpr(FileConfiguration config) {
        this.cooldown = config.getInt("commands.tpr.cooldown");
        this.range = config.getInt("commands.tpr.range");
        this.extendedRange = config.getInt("commands.tpr.extendedrange");
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        final Player player = (Player) sender;
        if (check(player, "tpr", cooldown)) {
            Bukkit.getScheduler().runTask(PopCraft.getPlugin(), () -> {
                player.teleport(
                        getRandomCoordinate(
                                player.hasPermission(EXTENDED_TPR_PERMISSION) ? this.extendedRange : this.range
                        )
                );
                Message.normal(player, "Teleporting to a random location...");
            });
        } else {
            Message.cooldown(player, "tpr", this.cooldown);
        }
        return true;
    }

    private static boolean notSafe(final Location location) {
        final Block block = location.getBlock();
        return UNSAFE_BIOMES.contains(block.getBiome()) || UNSAFE_MATERIALS.contains(block.getType());
    }

    private Location getRandomCoordinate(final int distance) {
        Location location;
        do {
            location = new Location(
                    Bukkit.getServer().getWorlds().get(0),
                    distance * randomDirection() * Math.random(),
                    0,
                    distance * randomDirection() * Math.random()
            );
            location.setY(location.getWorld().getHighestBlockYAt(location));
        } while (!notSafe(location));
        return location.add(0, 1, 0);
    }

    private static int randomDirection() {
        return Math.random() > 0.5 ? 1 : -1;
    }

}