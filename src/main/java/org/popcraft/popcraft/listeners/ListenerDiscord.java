package org.popcraft.popcraft.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ListenerDiscord extends PopCraftListener {

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        plugin.getDiscordIntegration().getDiscordCommandSender().sendMessage(
                String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage()));
    }

}
