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
        Entity target = event.getRightClicked();
        if (rider.getVehicle() == null && rider.getPassengers().isEmpty()) {
            // If the player isn't riding anything, and isn't being ridden, then they can ride something
            List<Entity> toRide = target.getPassengers();
            // Find a new target while the number of passengers for each is at capacity
            while (!(target instanceof Boat) && toRide.size() == 1 || toRide.size() == 2) {
                target = target.getPassengers().get(0);
                toRide = target.getPassengers();
            }
            if (isRideable(rider) && isRideable(target)) {
                target.addPassenger(rider);
            }
        } else if (rider.getPassengers().contains(target) && !isHolding(rider, Material.FIREWORK_ROCKET)) {
            // Otherwise, the player is probably trying to dismount someone
            rider.eject();
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
