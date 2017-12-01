package org.popcraft.popcraft;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static net.md_5.bungee.api.ChatColor.GOLD;
import static net.md_5.bungee.api.ChatColor.GREEN;
import static net.md_5.bungee.api.ChatColor.RESET;
import static org.popcraft.popcraft.PopCraft.getPlugin;

public enum Messages {

    DONATE(
            "donate",
            "You may donate at this site: %spopcraft.enjin.com/donate",
            GREEN
    ),

    DISCORD(
            "discord",
            "Click the link to join us on Discord: %shttps://discord.gg/98Cw8Mz",
            GREEN
    ),

    PLUGINS(
            "plugins",
            "%sPlugins (1): %sPopCraft",
            RESET,
            GREEN
    ),

    TEAM_SPEAK(
            "teamspeak",
            "TeamSpeak server address: %sts3.popcraft.org",
            GREEN
    ),

    VERSION(
            "version",
            "<GREEN>%s<RESET> version <GREEN>%s<RESET>%n Website: <GREEN>%s<RESET>%n Authors: "
                    .replaceAll("<GREEN>", GREEN.toString())
                    .replaceAll("<RESET>", RESET.toString()),
            getPlugin().getDescription().getName(),
            getPlugin().getDescription().getVersion(),
            getPlugin().getDescription().getWebsite(),
            getPlugin().getDescription().getAuthors()
                    .stream()
                    .map(author -> GREEN.toString() + author + RESET.toString())
                    .collect(Collectors.joining(", "))
    ),

    VOTE(
            "vote",
            "You can vote once per day at the following links:%n%s" +
                    "http://goo.gl/uJtnjS %n" +
                    "http://goo.gl/6xzd1f %n" +
                    "http://goo.gl/m7pagK %n" +
                    "%sThank you for supporting PopCraft!",
            GREEN,
            GOLD
    );

    private final String command;
    private final String message;

    Messages(final String command, final String message, final Object... arguments) {
        this.command = command;
        this.message = GOLD + format(message, arguments);
    }

    public CommandExecutor createExecutor() {
        return (commandSender, command, s, strings) -> {
            commandSender.sendMessage(this.message);
            return true;
        };
    }

    public static void registerCommands(final JavaPlugin plugin) {
        for (Messages message : Messages.values()) {
            checkNotNull(
                    plugin.getCommand(message.command),
                    "Plugin " + message.command + " was found null"
            ).setExecutor(message.createExecutor());
        }
    }

}
