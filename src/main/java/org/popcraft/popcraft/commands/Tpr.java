package org.popcraft.popcraft.commands;

import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.popcraft.popcraft.newCode.PopCommand;
import org.popcraft.popcraft.utils.Cooldown;
import org.popcraft.popcraft.utils.Message;

import java.util.Set;
import java.util.function.Function;

import static org.bukkit.Material.*;
import static org.bukkit.block.Biome.*;

@PopCommand("tpr")
public class Tpr extends PlayerCommand {

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
            CACTUS,
            STATIONARY_WATER,
            WATER
    );

    private final JavaPlugin plugin;

    private final int range;
    private final int extendedRange;

    private final Function<Player, Boolean> coolDownCheck;

    @Inject
    public Tpr(final JavaPlugin plugin, final FileConfiguration config) {
        this.plugin = plugin;
        this.range = config.getInt("commands.tpr.range");
        this.extendedRange = config.getInt("commands.tpr.extendedrange");
        this.coolDownCheck = Cooldown.defaultCooldown("tpr", config.getInt("commands.tpr.cooldown"));
    }

    @Override
    public boolean onPlayerCommand(Player player, Command cmd, String label, String[] args) {
        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
            player.teleport(
                    getRandomCoordinate(
                            player.hasPermission(EXTENDED_TPR_PERMISSION) ? this.extendedRange : this.range
                    )
            );
            Message.normal(player, "Teleporting to a random location...");
        });
        return true;
    }

    @Override
    public boolean playerCheck(Player player) {
        return this.coolDownCheck.apply(player);
    }

    private Location getRandomCoordinate(final int distance) {
        Location location;
        do {
            location = new Location(
                    this.plugin.getServer().getWorlds().get(0),
                    distance * randomDirection() * Math.random(),
                    0,
                    distance * randomDirection() * Math.random()
            );
            location.setY(location.getWorld().getHighestBlockYAt(location));
        } while (notSafe(location));
        return location.add(0, 1, 0);
    }

    private static boolean notSafe(final Location location) {
        final Block block = location.getBlock();
        return UNSAFE_BIOMES.contains(block.getBiome()) || UNSAFE_MATERIALS.contains(block.getType());
    }

    private static int randomDirection() {
        return Math.random() > 0.5 ? 1 : -1;
    }

}