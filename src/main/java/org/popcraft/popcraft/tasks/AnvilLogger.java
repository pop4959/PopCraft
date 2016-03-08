package org.popcraft.popcraft.tasks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import org.popcraft.popcraft.utils.Message;

public class AnvilLogger implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
	if (!e.isCancelled()) {
	    Inventory inv = e.getInventory();
	    if (inv instanceof AnvilInventory) {
		HumanEntity entity = e.getWhoClicked();
		if (entity instanceof Player) {
		    Player p = (Player) entity;
		    InventoryView view = e.getView();
		    int rawSlot = e.getRawSlot();
		    if (rawSlot == view.convertSlot(rawSlot)) {
			if (rawSlot == 2) {
			    ItemStack item = e.getCurrentItem();
			    if (item != null) {
				ItemMeta meta = item.getItemMeta();
				if (meta != null) {
				    if (meta.hasDisplayName()) {
					String itemName = meta.getDisplayName();
					try {
					    File anvilfile = new File("anvil.txt");
					    if (!anvilfile.exists()) {
						anvilfile.createNewFile();
					    }
					    FileWriter anvilwriter = new FileWriter(anvilfile, true);
					    BufferedWriter signbuffered = new BufferedWriter(anvilwriter);
					    String playername = p.getName();
					    String timestamp = Message.getCurrentTime();
					    signbuffered.append(playername + " - " + timestamp + " - " + itemName);
					    signbuffered.newLine();
					    signbuffered.close();
					} catch (IOException e2) {
					    e2.printStackTrace();
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
