package org.popcraft.popcraft.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * Listens for miscellaneous protection related events
 */
public class ListenerProtection extends PopCraftListener {

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (config.getBoolean("protection.preventEnderCrystalExplosion")) {
            if (EntityType.ENDER_CRYSTAL.equals(event.getEntityType())) {
                event.setCancelled(true);
            }
        }
    }

}
