package org.popcraft.popcraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.Cooldown;
import org.popcraft.popcraft.utils.TeleportUtil;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class CommandTpr extends PopCraftCommand {

    private Cooldown cooldown = new Cooldown(plugin.getConfig().getLong("command.tpr.cooldown"));

    private final EnumSet<Biome> UNSAFE_BIOMES = EnumSet.of(Biome.COLD_OCEAN, Biome.DEEP_COLD_OCEAN,
            Biome.DEEP_FROZEN_OCEAN, Biome.DEEP_LUKEWARM_OCEAN, Biome.DEEP_OCEAN, Biome.FROZEN_OCEAN,
            Biome.FROZEN_RIVER, Biome.LUKEWARM_OCEAN, Biome.OCEAN, Biome.RIVER, Biome.WARM_OCEAN);

    private final EnumSet<Material> UNSAFE_BLOCKS = EnumSet.of(Material.LAVA, Material.FIRE, Material.CACTUS,
            Material.MAGMA_BLOCK, Material.CAMPFIRE, Material.SWEET_BERRY_BUSH, Material.WITHER_ROSE);

    public CommandTpr() {
        super("tpr");
    }

    @Override
    public Result execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return Result.UNSUPPORTED_SENDER;
        }
        Player player = (Player) sender;
        if (cooldown.isFinished(player)) {
            boolean extended = player.hasPermission("popcraft.tpr.extended");
            int range = plugin.getConfig().getInt(extended ? "command.tpr.extendedRange" : "command.tpr.range");
            int minRange = plugin.getConfig().getInt("command.tpr.minimumRange");
            Location location = findRandomSafeLocation(range, minRange);
            TeleportUtil.teleport(command, player, location);
            cooldown.set(player);
            player.sendMessage(plugin.getMessage("teleportingRandomly"));
        } else {
            player.sendMessage(plugin.getMessage("commandOnCooldown", cooldown.getFormattedTimeRemaining(player)));
        }
        return Result.SUCCESS;
    }

    private Location findRandomSafeLocation(int range, int minRange) {
        double[] direction = {Math.random() > 0.5 ? 1 : -1, Math.random() > 0.5 ? 1 : -1};
        Location location = new Location(Bukkit.getServer().getWorlds().get(0),
                direction[0] * (minRange + (range - minRange) * Math.random()),
                0,
                direction[1] * (minRange + (range - minRange) * Math.random()));
        location.setY(Objects.requireNonNull(location.getWorld()).getHighestBlockYAt(location) + 1);
        return UNSAFE_BIOMES.contains(location.getBlock().getBiome())
                || UNSAFE_BLOCKS.contains(location.add(0, -1, 0).getBlock().getType())
                ? findRandomSafeLocation(range, minRange) : location;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }

}
