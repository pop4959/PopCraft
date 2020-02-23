package org.popcraft.popcraft.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.PlayerInventory;
import org.popcraft.popcraft.commands.CommandPiggyback;

import java.util.Arrays;
import java.util.List;

public class ListenerPiggyback extends PopCraftListener {

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractAtEntityEvent event) {
        Player rider = event.getPlayer();
        // The player shouldn't be able to use piggyback even if they had it enabled, if they do not have permission
        if (!rider.hasPermission("popcraft.piggyback")) {
            return;
        }
        Entity target = event.getRightClicked();
        if (rider.getVehicle() == null && rider.getPassengers().isEmpty()) {
            // If the player isn't riding anything, and isn't being ridden, then they can ride / be ridden
            if (rider.isSneaking()) {
                // If sneaking and the entity isn't a monster, have the entity ride you instead
                if (isRideable(rider) && isRideable(target)) {
                    rider.addPassenger(target);
                }
            } else {
                // Otherwise, just ride the entities
                List<Entity> toRide = target.getPassengers();
                // Find a new target while the number of passengers for each is at capacity
                while (!(target instanceof Boat) && toRide.size() == 1 || toRide.size() == 2) {
                    target = target.getPassengers().get(0);
                    toRide = target.getPassengers();
                }
                if (isRideable(rider) && isRideable(target)) {
                    target.addPassenger(rider);
                }
            }
        } else if (!rider.isSneaking() && !isHolding(rider, Material.FIREWORK_ROCKET)) {
            // Otherwise, the player is probably trying to dismount someone
            // Target may not be the correct entity so we need to check if the rider is lower down
            while (target.getVehicle() != null && !target.getVehicle().equals(rider)) {
                target = target.getVehicle();
            }
            if (rider.getPassengers().contains(target)) {
                rider.eject();
            }
        }
    }

    private boolean isRideable(Entity entity) {
        return ((CommandPiggyback) plugin.getCommands().get("piggyback")).isRideable(entity);
    }

    private boolean isHolding(Player player, Material... materials) {
        PlayerInventory playerInventory = player.getInventory();
        return Arrays.stream(materials)
                .anyMatch(m -> m.equals(playerInventory.getItemInMainHand().getType()) ||
                        m.equals(playerInventory.getItemInOffHand().getType()));
    }

}
