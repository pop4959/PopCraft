package org.popcraft.popcraft.tasks;

import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AnvilColor implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
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
                                    if (meta != null) {
                                        if (meta.hasDisplayName()) {
                                            ItemMeta m = item.getItemMeta();
                                            String displayName = meta.getDisplayName();
                                            if (!p.hasPermission("popcraft.anvilcolor.magic")
                                                    && displayName.contains("&k"))
                                                displayName.replace("&k", "");
                                            m.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
                                            item.setItemMeta(m);
                                            e.setCurrentItem(item);
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
}
