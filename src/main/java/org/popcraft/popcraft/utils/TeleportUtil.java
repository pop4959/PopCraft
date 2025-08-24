package org.popcraft.popcraft.utils;

import com.earth2me.essentials.AsyncTeleport;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.Trade;
import io.papermc.lib.PaperLib;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.popcraft.popcraft.PopCraft;

import java.util.concurrent.CompletableFuture;

public class TeleportUtil {

    public static void teleport(Command command, Player player, Location location) {
        try {
            Essentials essentials = PopCraft.getPlugin().getEssentials();
            final Trade charge = new Trade(command.getName(), essentials);
            AsyncTeleport teleport = essentials.getUser(player).getAsyncTeleport();
            teleport.setTpType(AsyncTeleport.TeleportType.NORMAL);
            teleport.teleport(location, charge, PlayerTeleportEvent.TeleportCause.COMMAND, new CompletableFuture<>());
        } catch (Exception e) {
            PaperLib.teleportAsync(player, location);
        }
    }

}
