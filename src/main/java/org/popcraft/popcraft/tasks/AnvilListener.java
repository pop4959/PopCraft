package org.popcraft.popcraft.tasks;

import lombok.extern.log4j.Log4j2;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

@Log4j2
public class AnvilListener implements Listener {

    @EventHandler
    public void onPrepareAnvilEvent(final PrepareAnvilEvent event) {
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

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.isCancelled()) {
            Inventory inv = e.getInventory();
            if (inv instanceof AnvilInventory) {
                HumanEntity entity = e.getWhoClicked();
                if (entity instanceof Player) {
                    Player p = (Player) entity;
                    if (p.hasPermission("popcraft.anvilcolor")) {
                        InventoryView view = e.getView();
                        int rawSlot = e.getRawSlot();
                        if (rawSlot == view.convertSlot(rawSlot)) {
                            if (rawSlot == 2) {
                                ItemStack item = e.getCurrentItem();
                                if (item != null) {
                                    ItemMeta meta = item.getItemMeta();
                                    if (meta != null && meta.hasDisplayName()) {
                                        LOGGER.info("{} - {}", p.getName(), meta.getDisplayName());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
