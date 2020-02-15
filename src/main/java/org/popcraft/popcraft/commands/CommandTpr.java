package org.popcraft.popcraft.commands;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.Teleport;
import com.earth2me.essentials.Trade;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.popcraft.popcraft.Cooldown;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class CommandTpr extends PopCraftCommand {

    private Cooldown cooldown = new Cooldown(plugin.getConfig().getLong("command.tpr.cooldown"));

    private final EnumSet<Biome> UNSAFE_BIOMES = EnumSet.of(Biome.COLD_OCEAN, Biome.DEEP_COLD_OCEAN,
            Biome.DEEP_FROZEN_OCEAN, Biome.DEEP_LUKEWARM_OCEAN, Biome.DEEP_OCEAN, Biome.DEEP_WARM_OCEAN,
            Biome.FROZEN_OCEAN, Biome.FROZEN_RIVER, Biome.LUKEWARM_OCEAN, Biome.OCEAN, Biome.RIVER, Biome.WARM_OCEAN);

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
            Location location = findRandomSafeLocation(range);
            try {
                Essentials essentials = plugin.getEssentials();
                final Trade charge = new Trade(command.getName(), essentials);
                Teleport teleport = essentials.getUser(player).getTeleport();
                teleport.setTpType(Teleport.TeleportType.NORMAL);
                teleport.teleport(location, charge, PlayerTeleportEvent.TeleportCause.COMMAND);
            } catch (Exception e) {
                PaperLib.teleportAsync(player, location);
            }
            cooldown.set(player);
            player.sendMessage(plugin.getMessage("teleporting"));
        } else {
            player.sendMessage(plugin.getMessage("commandOnCooldown", cooldown.getFormattedTimeRemaining(player)));
        }
        return Result.SUCCESS;
    }

    private Location findRandomSafeLocation(int range) {
        double[] direction = {Math.random() > 0.5 ? 1 : -1, Math.random() > 0.5 ? 1 : -1};
        Location location = new Location(Bukkit.getServer().getWorlds().get(0),
                direction[0] * range * Math.random(),
                0,
                direction[1] * range * Math.random());
        location.setY(Objects.requireNonNull(location.getWorld()).getHighestBlockYAt(location) + 1);
        return UNSAFE_BIOMES.contains(location.getBlock().getBiome())
                || UNSAFE_BLOCKS.contains(location.add(0, -1, 0).getBlock().getType())
                ? findRandomSafeLocation(range) : location;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }

}
