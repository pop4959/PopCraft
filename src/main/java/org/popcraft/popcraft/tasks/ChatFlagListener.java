package org.popcraft.popcraft.tasks;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.popcraft.popcraft.utils.FlagTrie;

import java.util.regex.Pattern;

import static org.popcraft.popcraft.PopCraftModule.COMMAND_TRIE_KEY;
import static org.popcraft.popcraft.PopCraftModule.FLAG_TRIE_KEY;

@Slf4j
public class ChatFlagListener implements Listener {

    private final Pattern IP_MATCH = Pattern.compile("(?:\\s*\\d+\\s*\\.){3}\\s*\\d+\\s*");
    private final FileConfiguration config;
    private final FlagTrie flagTrie;
    private final FlagTrie commandTrie;

    @Inject
    public ChatFlagListener(
            final FileConfiguration config,
            @Named(FLAG_TRIE_KEY) final FlagTrie flagTrie,
            @Named(COMMAND_TRIE_KEY) final FlagTrie commandTrie
    ) {
        this.config = config;
        this.flagTrie = flagTrie;
        this.commandTrie = commandTrie;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        this.log(event.getMessage(), event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        String name = event.getPlayer().getName();
        String[] command = message.split(" ");
        if (command.length > 1 && this.commandTrie.containsFlag(command[0])) {
            this.log(message, name);
        }
    }

    private void log(final String message, final String name) {
        if (flag(message)) {
            LOGGER.info("{} - {}", name, message);
        }
    }

    public boolean flag(String message) {
        if (this.flagTrie.containsFlag(message)) {
            return true;
        }
        return this.flagTrie.containsFlag(message) || this.ipCheck(message);
    }

    private boolean ipCheck(final String message) {
        return this.config.getBoolean("jonslogger.checkip") && this.IP_MATCH.matcher(message).find();
    }

}
