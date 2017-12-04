package org.popcraft.popcraft.commands;

import com.google.common.collect.Range;
import org.bukkit.command.Command;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.popcraft.popcraft.newCode.PopCommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static net.md_5.bungee.api.ChatColor.GOLD;
import static net.md_5.bungee.api.ChatColor.RED;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@PopCommand("piggyback")
public class Piggyback extends PlayerCommand implements Listener {

    private final Map<UUID, Boolean> piggyback = new HashMap<>();

    @Override
    public boolean onPlayerCommand(Player player, Command cmd, String label, String[] args) {
        final boolean rideable = !this.canPiggyBack(player);
        this.piggyback.put(player.getUniqueId(), rideable);
        player.sendMessage(format("%sPiggyback %s%s%s.", GOLD, RED, rideable ? "enabled" : "disabled", GOLD));
        return true;
    }

    private boolean canPiggyBack(final Entity player) {
        return !(player instanceof Player) || this.piggyback.getOrDefault(player.getUniqueId(), false);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        final Player rider = e.getPlayer();
        if (this.canPiggyBack(rider)) {
            final Entity target = e.getRightClicked();
            rideTarget(rider, target);
        }
    }

    private void rideTarget(Player rider, Entity target) {
        if (rider.getVehicle() == null && isEmpty(rider.getPassengers())) {
            final Entity topMostEntity = this.getTopMostEntity(target);
            if (this.canPiggyBack(topMostEntity)) {
                topMostEntity.addPassenger(rider);
            }
        } else if (rider.getPassengers().contains(target)) {
            rider.eject();
        }
    }

    private Entity getTopMostEntity(final Entity entity) {
        final List<Entity> passengers = entity.getPassengers();
        if (!isEmpty(passengers)) {
            //TODO: How to address mutliple passengers
            return this.getTopMostEntity(passengers.get(0));
        }
        return entity;
    }

}