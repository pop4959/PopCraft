package org.popcraft.popcraft.listeners;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.popcraft.popcraft.utils.FileUtil;
import org.popcraft.popcraft.utils.TimeUtil;

/**
 * Listens for events which may be logged
 */
public class ListenerLogging extends PopCraftListener {

    @EventHandler
    public void onPlayerEditBook(PlayerEditBookEvent event) {
        log("books.txt", event.getPlayer().getName(), TimeUtil.getCurrentTime(),
                event.getNewBookMeta().getPages().toString().replace("[", "\"").replace("]", "\""));
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Location location = event.getBlock().getLocation();
        log("signs.txt", event.getPlayer().getName(), TimeUtil.getCurrentTime(),
                "\"" + StringUtils.join(event.getLines(), ' ') + "\"",
                "(" + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ() + ")");
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (!(inventory instanceof AnvilInventory) || !(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (event.getRawSlot() != 2 || event.getRawSlot() != event.getView().convertSlot(event.getRawSlot())
                || event.getCurrentItem() == null) {
            return;
        }
        ItemMeta itemMeta = event.getCurrentItem().getItemMeta();
        if (itemMeta == null || !itemMeta.hasDisplayName()) {
            return;
        }
        log("anvil.txt", player.getName(), TimeUtil.getCurrentTime(), itemMeta.getDisplayName());
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (config.getStringList("flagger.flags").stream().anyMatch(flag -> event.getMessage().contains(flag))) {
            log("flag.txt", TimeUtil.getCurrentTime(), event.getPlayer().getName(), event.getMessage());
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (config.getStringList("flagger.flags").stream().anyMatch(flag -> event.getMessage().contains(flag))) {
            log("flag.txt", TimeUtil.getCurrentTime(), event.getPlayer().getName(), event.getMessage());
        }
        String message = event.getMessage();
        String[] tokens = message.split(" ");
        if (tokens.length > 1 && "/ban".equalsIgnoreCase(tokens[0])) {
            String reason = tokens.length > 2 ? message.substring(message.indexOf(tokens[2])) : plugin.getMessage("ban");
            log("ban.txt", TimeUtil.getCurrentTime(), event.getPlayer().getName(), tokens[1], reason);
        }
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        String message = event.getCommand();
        String[] tokens = message.split(" ");
        if (tokens.length > 1 && "/ban".equalsIgnoreCase(tokens[0])) {
            String reason = tokens.length > 2 ? message.substring(message.indexOf(tokens[2])) : plugin.getMessage("ban");
            log("ban.txt", TimeUtil.getCurrentTime(), plugin.getMessage("console"), tokens[1], reason);
        }
    }

    private void log(String filename, String... fields) {
        FileUtil.writeLine(filename, StringUtils.join(fields, " - "));
    }

}
