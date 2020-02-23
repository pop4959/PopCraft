package org.popcraft.popcraft.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.popcraft.popcraft.commands.CommandTicket;

import java.util.List;
import java.util.Objects;

/**
 * Listens for miscellaneous player related events
 */
public class ListenerPlayer extends PopCraftListener {

    private final List<String> PROFANITY = config.getStringList("profanity");

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Welcome the player
        if (Bukkit.getServer().getOfflinePlayer(player.getUniqueId()).hasPlayedBefore()) {
            event.setJoinMessage(plugin.getMessage("join", player.getDisplayName()));
        } else {
            event.setJoinMessage(plugin.getMessage("welcome", player.getDisplayName()));
            Bukkit.getScheduler().runTaskLater(plugin, () -> player.sendMessage(plugin.getMessage("welcomeTips")), 10L);
        }
        // Set the player's TAB list header and footer
        player.setPlayerListHeaderFooter(plugin.getMessage("playerListHeader"), plugin.getMessage("playerListFooter"));
        // Move the player to the appropriate team
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        if (scoreboardManager != null && !player.hasPermission("popcraft.tab.staff")) {
            String teamName = player.hasPermission("popcraft.tab.donator") ? config.getString("teams.donator") : config.getString("teams.default");
            Team team = scoreboardManager.getMainScoreboard().getTeam(Objects.requireNonNull(teamName));
            if (team != null) {
                team.addEntry(player.getName());
            }
        }
        CommandTicket ticketCommand = (CommandTicket) plugin.getCommands().get("ticket");
        Bukkit.getScheduler().runTaskLater(plugin, () -> ticketCommand.notifyStaff(player), 20L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(plugin.getMessage("quit", event.getPlayer().getDisplayName()));
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        filter(event, event.getPlayer(), event.getMessage());
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        filter(event, event.getPlayer(), event.getMessage());
    }

    private void filter(Cancellable event, Player player, String content) {
        if (!player.hasPermission("popcraft.staff")) {
            String message = content.toLowerCase();
            for (String word : PROFANITY) {
                if (message.contains(word.toLowerCase())) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.kickPlayer(plugin.getMessage("kick", plugin.getMessage("swearingNotAllowed")));
                        for (Player toNotify : Bukkit.getOnlinePlayers()) {
                            if (toNotify.hasPermission("popcraft.staff.notify")) {
                                toNotify.sendMessage(plugin.getMessage("swearingNotification", player.getDisplayName(), content));
                            }
                        }
                    });
                    event.setCancelled(true);
                }
            }
        }
    }

}
