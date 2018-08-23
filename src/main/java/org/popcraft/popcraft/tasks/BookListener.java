package org.popcraft.popcraft.tasks;

import lombok.extern.log4j.Log4j2;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;

@Log4j2
public class BookListener implements Listener {

    @EventHandler
    public void onPlayerEditBookEvent(PlayerEditBookEvent event) {
        final String playername = event.getPlayer().getName();
        final String booktext = event.getNewBookMeta().getPages().toString().replace("[", "\"").replace("]", "\"");
        LOGGER.info("{} - {}", playername, booktext);
    }

}
