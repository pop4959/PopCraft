package org.popcraft.popcraft.integration.discord;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.popcraft.popcraft.PopCraft;

import javax.security.auth.login.LoginException;

public class DiscordIntegration extends ListenerAdapter {

    private PopCraft plugin;
    private JDA jda;
    private DiscordCommandSender discordCommandSender;
    private long lastSender = 0;
    private final long CHANNEL_ID;

    public DiscordIntegration(PopCraft plugin) {
        this.plugin = plugin;
        try {
            this.jda = new JDABuilder(AccountType.BOT).setToken(plugin.getConfig().getString("discord.token"))
                    .setAutoReconnect(true).setActivity(Activity.playing("Minecraft")).build();
            this.jda.addEventListener(this);
        } catch (LoginException e) {
            this.jda = null;
            e.printStackTrace();
        }
        this.discordCommandSender = new DiscordCommandSender(this);
        CHANNEL_ID = plugin.getConfig().getLong("discord.channel");
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.getChannel().getIdLong() != CHANNEL_ID) {
            return;
        }
        this.lastSender = event.getAuthor().getIdLong();
        String contentStripped = event.getMessage().getContentStripped();
        Bukkit.broadcastMessage(
                plugin.getMessage("chatFormat",
                        plugin.getMessage("discordPrefix") + this.discordCommandSender.getName(),
                        contentStripped));
        event.getMessage().delete().queue();
    }

    public PopCraft getPlugin() {
        return plugin;
    }

    public JDA getJda() {
        return jda;
    }

    public DiscordCommandSender getDiscordCommandSender() {
        return discordCommandSender;
    }

    public long getLastSender() {
        return lastSender;
    }

    public long getChannelId() {
        return CHANNEL_ID;
    }
}
