package org.popcraft.popcraft.listeners;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.popcraft.popcraft.utils.FileUtil;
import org.popcraft.popcraft.utils.TimeUtil;

/**
 * Listens for events which may be logged
 */
public class ListenerLogging extends PopCraftListener {

    private final String BOOK_FILENAME = "books.txt";
    private final String SIGN_FILENAME = "signs.txt";
    private final String FIELD_SEPARATOR = " - ";

    @EventHandler
    public void onPlayerEditBook(PlayerEditBookEvent event) {
        String[] fields = new String[]{
                event.getPlayer().getName(),
                TimeUtil.getCurrentTime(),
                event.getNewBookMeta().getPages().toString().replace("[", "\"").replace("]", "\"")
        };
        FileUtil.writeLine(BOOK_FILENAME, StringUtils.join(fields, FIELD_SEPARATOR));
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Location location = event.getBlock().getLocation();
        String[] fields = new String[]{
                event.getPlayer().getName(),
                TimeUtil.getCurrentTime(),
                "\"" + StringUtils.join(event.getLines(), ' ') + "\"",
                "(" + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ() + ")"
        };
        FileUtil.writeLine(SIGN_FILENAME, StringUtils.join(fields, FIELD_SEPARATOR));
    }

}
