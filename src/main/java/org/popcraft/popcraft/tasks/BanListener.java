package org.popcraft.popcraft.tasks;

import com.google.inject.Inject;
import lombok.extern.log4j.Log4j2;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

@Log4j2
public class BanListener implements Listener {

    private final FileConfiguration config;

    @Inject
    public BanListener(final FileConfiguration config) {
        this.config = config;
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        this.checkReport(event.getMessage(), event.getPlayer().getName());
    }

    @EventHandler
    public void onConsoleCommand(ServerCommandEvent event) {
        this.checkReport(event.getCommand(), "Server");
    }

    private void checkReport(final String message, final String name) {
        final String[] tokens = message.split(" ");
        if (tokens.length <= 1 || !this.config.getBoolean("jonslogger.showban")) {
            return;
        }

        if (tokens[0].contains("ban") || tokens[0].contains("pardon")) {
            final String victim = tokens[1];
            final String reason = tokens.length >= 3 ? message.substring(message.indexOf(tokens[2])) : "The Ban Hammer has spken!";
            LOGGER.info("{} - {} - {} - {}", tokens[0], name, victim, reason);
        }

    }


}
