package org.popcraft.popcraft.tasks;

import lombok.extern.slf4j.Slf4j;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

@Slf4j
public class SignListener implements Listener {

    @EventHandler
    public void SignChangeEvent(final SignChangeEvent event) {
        final String playername = event.getPlayer().getName();
        final String signtext = "\"" + event.getLine(0) + " " + event.getLine(1) + " " + event.getLine(2) + " "
                + event.getLine(3) + "\"";
        final String location = "[" + Math.round(event.getBlock().getLocation().getX()) + ","
                + Math.round(event.getBlock().getLocation().getY()) + ","
                + Math.round(event.getBlock().getLocation().getZ()) + "]";
        LOGGER.info("{} - {} - {}", playername, location, signtext);
    }

}
