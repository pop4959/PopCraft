package org.popcraft.popcraft;

import com.earth2me.essentials.Essentials;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.popcraft.popcraft.commands.*;
import org.popcraft.popcraft.listeners.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public final class PopCraft extends JavaPlugin {

    private static PopCraft plugin;
    private Properties messages;
    private Map<String, PopCraftCommand> commands = new HashMap<>();
    private Essentials essentials;
    private Permission permissions;
    private Chat chat;

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
            if (messageFile.exists()) {
                this.messages.load(new FileReader(messageFile));
            }
        } catch (IOException e) {
            this.getLogger().severe("Failed to load messages");
        }
        // Register events
        registerEvents(new ListenerAnvil(), new ListenerDrops(), new ListenerLogging(), new ListenerPiggyback(),
                new ListenerPlayer(), new ListenerProtection(), new ListenerPvp(), new ListenerScoreboard(),
                new ListenerTrail());
        if (this.getServer().getPluginManager().getPlugin("Votifier") != null) {
            registerEvents(new ListenerVotifier());
        }
        // Register commands
        registerCommands(new CommandAura(), new CommandDiscord(), new CommandDonate(), new CommandFireworks(),
                new CommandHandicap(), new CommandMe(), new CommandMusic(), new CommandPiggyback(),
                new CommandPlugins(), new CommandPop(), new CommandPvp(), new CommandResourcepack(), new CommandSay(),
                new CommandSpoof(), new CommandStaff(), new CommandTicket(), new CommandTpr(), new CommandTrail(),
                new CommandTransferscores(), new CommandVersion(), new CommandVote());
        // Get Essentials API
        Plugin essentialsPlugin = this.getServer().getPluginManager().getPlugin("Essentials");
        if (essentialsPlugin != null) {
            this.essentials = (Essentials) essentialsPlugin;
        }
        // Set up Vault API
        if (this.getServer().getPluginManager().getPlugin("Vault") != null) {
            // Chat
            RegisteredServiceProvider<Chat> registeredServiceProviderChat = this.getServer()
                    .getServicesManager().getRegistration(Chat.class);
            if (registeredServiceProviderChat != null) {
                this.chat = registeredServiceProviderChat.getProvider();
            }
            // Permissions
            RegisteredServiceProvider<Permission> registeredServiceProviderPermissions = this.getServer()
                    .getServicesManager().getRegistration(Permission.class);
            if (registeredServiceProviderPermissions != null) {
                this.permissions = registeredServiceProviderPermissions.getProvider();
            }
        }
    }

    @Override
    public void onDisable() {
        // Unregister any events
        HandlerList.unregisterAll(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PopCraftCommand cmd = this.commands.get(command.getName());
        if (cmd == null) {
            sender.sendMessage(this.getMessage("commandNotFound"));
            return true;
        }
        Result result = cmd.execute(sender, command, label, args);
        if (result == null || result.equals(Result.FAILURE)) {
            sender.sendMessage(this.getMessage("commandFailure"));
        } else if (result.equals(Result.INCORRECT_USAGE)) {
            sender.sendMessage(command.getDescription());
            sender.sendMessage(command.getUsage().replaceAll("<command>", label));
        } else if (result.equals(Result.UNSUPPORTED_SENDER)) {
            sender.sendMessage(this.getMessage("unsupportedSender", sender.getClass().getSimpleName()));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        PopCraftCommand cmd = this.commands.get(command.getName());
        if (cmd == null) {
            return Collections.emptyList();
        }
        return cmd.onTabComplete(sender, command, alias, args);
    }

    public static PopCraft getPlugin() {
        return plugin;
    }

    public void registerEvents(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    public void registerCommands(PopCraftCommand... commands) {
        for (PopCraftCommand command : commands) {
            this.commands.put(command.getName(), command);
        }
    }

    public String getMessage(String key, Object... args) {
        String formattedMessage = String.format(messages.getProperty(key), args);
        return ChatColor.translateAlternateColorCodes('&', formattedMessage);
    }

    public Map<String, PopCraftCommand> getCommands() {
        return commands;
    }

    public Essentials getEssentials() {
        return this.essentials;
    }

    public Permission getPermissions() {
        return this.permissions;
    }

    public Chat getChat() {
        return this.chat;
    }

}
