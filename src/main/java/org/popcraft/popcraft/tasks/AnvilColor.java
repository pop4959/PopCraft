package org.popcraft.popcraft.tasks;

import com.google.inject.Inject;
import org.bukkit.Material;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.popcraft.popcraft.utils.Message;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

import static org.bukkit.ChatColor.*;

public class AnvilColor implements Listener {

    private final JavaPlugin plugin;
    private final Logger logger;

    @Inject
    public AnvilColor(final JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    @EventHandler
    public void onPrepareAnvilEvent(PrepareAnvilEvent e) {
        String renameText = translateAlternateColorCodes(
                '&',
                e.getInventory().getRenameText().replace("&k", "")
        );

        final ItemMeta meta = e.getResult().getItemMeta();
        if (meta != null) {
            meta.setDisplayName(renameText);
            e.getResult().setItemMeta(meta);
        }

        final ItemStack[] anvilContents = e.getView().getTopInventory().getContents();
        if (anvilContents[0] == null &&
                anvilContents[1] == null &&
                e.getViewers().stream().map(HumanEntity::getItemOnCursor).anyMatch(Objects::nonNull) &&
                e.getInventory().getRenameText() == null) {
            this.logger.info("Rename has occurred");
            this.plugin.getServer().broadcastMessage("Rename has occurred");
        }
    }

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
                                    if (meta != null && meta.hasDisplayName()) {
                                        item.setItemMeta(colorItemMeta(p, meta));
                                        writeName(p, item.getItemMeta());
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

    public void writeName(final Player player, final ItemMeta meta) {
        String itemName = meta.getDisplayName();
        try {
            File anvilfile = new File("anvil.txt");
            if (!anvilfile.exists()) {
                anvilfile.createNewFile();
            }
            FileWriter anvilwriter = new FileWriter(anvilfile, true);
            BufferedWriter signbuffered = new BufferedWriter(anvilwriter);
            String playername = player.getName();
            String timestamp = Message.getCurrentTime();
            signbuffered.append(playername + " - " + timestamp + " - " + itemName);
            signbuffered.newLine();
            signbuffered.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public ItemMeta colorItemMeta(final Player player, final ItemMeta meta) {
        String displayName = meta.getDisplayName();
        if (!player.hasPermission("popcraft.anvilcolor.magic") && displayName.contains("&k")) {
            displayName = displayName.replace("&k", "");
        }
        meta.setDisplayName(translateAlternateColorCodes('&', displayName));
        return meta;
    }

}
