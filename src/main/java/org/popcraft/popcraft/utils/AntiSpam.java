package org.popcraft.popcraft.utils;

import com.google.inject.Inject;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AntiSpam implements Listener {

    private final JavaPlugin plugin;
    private final Server server;
    private final FileConfiguration config;
    private final Cooldown cooldown;

    @Inject
    public AntiSpam(final JavaPlugin plugin, final Server server, final FileConfiguration config) {
        this.plugin = plugin;
        this.server = server;
        this.config = config;
        this.cooldown = new Cooldown(config.getInt("antispam.cooldown"));
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(this.filterMessage(
                event.getPlayer(),
                event.getMessage().toLowerCase(),
                true
        ));
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        event.setCancelled(this.filterMessage(
                event.getPlayer(),
                event.getMessage().toLowerCase(),
                !event.getPlayer().hasPermission("popcraft.staff")
        ));
    }

    private boolean filterMessage(final Player player, final String message, final boolean disallowProfanity) {
        boolean containsProfanity = this.config.getStringList("profanityprotect")
                .stream()
                .map(String::toLowerCase)
                .anyMatch(message::contains);

        if (disallowProfanity && containsProfanity) {
            this.kickPlayer(player, "Swearing is not allowed on this server!");
        } else if (config.getBoolean("antispam.enabled") && this.cooldown.check(player.getUniqueId())) {
            this.kickPlayer(player, "Spamming is not allowed on this server!");
        } else {
            return false;
        }
        return true;
    }

    private void kickPlayer(final Player player, final String message) {
        this.server.getScheduler().runTask(this.plugin, () -> Message.kick(player, message));
    }

}
