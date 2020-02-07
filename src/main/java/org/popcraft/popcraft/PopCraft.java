package org.popcraft.popcraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.popcraft.popcraft.listeners.ListenerDrops;
import org.popcraft.popcraft.listeners.ListenerLogging;
import org.popcraft.popcraft.listeners.ListenerPlayer;
import org.popcraft.popcraft.listeners.ListenerProtection;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Properties;

public final class PopCraft extends JavaPlugin {

    private static PopCraft plugin;
    private Properties messages;

    @Override
    public void onEnable() {
        plugin = this;
        // Load and save any missing config values
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        File messageFile = new File(this.getDataFolder() + File.separator + "messages.properties");
        try {
            Properties defaultMessages = new Properties();
            defaultMessages.load(new InputStreamReader(Objects.requireNonNull(this.getResource("messages.properties"))));
            this.messages = new Properties(defaultMessages);
            this.messages.load(new FileReader(messageFile));
        } catch (IOException e) {
            this.getLogger().severe("Failed to load messages");
        }
        // Register events
        registerEvents(new ListenerDrops(), new ListenerLogging(), new ListenerPlayer(), new ListenerProtection());
    }

    @Override
    public void onDisable() {
        // Unregister any events
        HandlerList.unregisterAll(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return super.onCommand(sender, command, label, args);
    }

    public static PopCraft getPlugin() {
        return plugin;
    }

    public void registerEvents(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    public String getMessage(String key, Object... args) {
        String formattedMessage = String.format(messages.getProperty(key), args);
        return ChatColor.translateAlternateColorCodes('&', formattedMessage);
    }

}
