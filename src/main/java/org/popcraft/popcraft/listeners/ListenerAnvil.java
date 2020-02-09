package org.popcraft.popcraft.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Listens to anvil renaming to allow for color codes to be inserted
 */
public class ListenerAnvil extends PopCraftListener {

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        String renameText = event.getInventory().getRenameText();
        if (event.getViewers().stream()
                .noneMatch(humanEntity -> humanEntity.hasPermission("popcraft.anvilcolor"))
                || renameText == null || event.getResult() == null) {
            return;
        }
        if (event.getViewers().stream()
                .noneMatch(humanEntity -> humanEntity.hasPermission("popcraft.anvilcolor.magic"))) {
            renameText = renameText.replace("&k", "");
        }
        renameText = ChatColor.translateAlternateColorCodes('&', renameText);
        ItemMeta itemMeta = event.getResult().getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(renameText);
            event.getResult().setItemMeta(itemMeta);
        }
    }

}
