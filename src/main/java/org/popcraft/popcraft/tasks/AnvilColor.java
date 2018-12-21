package org.popcraft.popcraft.tasks;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.meta.ItemMeta;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public class AnvilColor implements Listener {

    @EventHandler
    public void onPrepareAnvilEvent(final PrepareAnvilEvent event) {
        for (HumanEntity h : event.getViewers()) {
            if (!h.hasPermission("popcraft.anvilcolor")) {
                return;
            }
        }

        final String renameText = translateAlternateColorCodes('&', this.getCleanedName(event));
        final ItemMeta meta = event.getResult().getItemMeta();
        if (meta != null) {
            meta.setDisplayName(renameText);
            event.getResult().setItemMeta(meta);
        }
    }

    protected String getCleanedName(final PrepareAnvilEvent event) {
        final String renamedText = event.getInventory().getRenameText();

        final boolean everyoneHasPermissions = event.getViewers()
                .stream()
                .map(entity -> entity.hasPermission("popcraft.anvilcolor.magic"))
                .reduce(Boolean.FALSE, (left, right) -> left && right);

        if (!everyoneHasPermissions && renamedText != null) {
            return renamedText.replace("&k", "");
        }
        return renamedText == null ? "" : renamedText;
    }
}
