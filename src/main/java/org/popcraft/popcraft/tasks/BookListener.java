package org.popcraft.popcraft.tasks;

import lombok.extern.slf4j.Slf4j;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;

@Slf4j
public class BookListener implements Listener {

    @EventHandler
    public void onPlayerEditBookEvent(PlayerEditBookEvent event) {
        final String playername = event.getPlayer().getName();
        final String booktext = event.getNewBookMeta().getPages().toString().replace("[", "\"").replace("]", "\"");
        LOGGER.info("{} - {}", playername, booktext);
    }

}
