package org.popcraft.popcraft.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.ScoreboardManager;
import org.popcraft.popcraft.commands.CommandPvp;

/**
 * Custom scoreboard for the server
 */
public class ListenerScoreboard extends PopCraftListener {

    private final String OBJECTIVE = plugin.getConfig().getString("scoreboard.name");
    private final int DEATH_PENALTY = plugin.getConfig().getInt("scoreboard.deathPenalty");

    @EventHandler
    public void onPlayerExpChange(PlayerExpChangeEvent event) {
        Score score = getScore(event.getPlayer());
        if (score != null && event.getAmount() > 0) {
            score.setScore(score.getScore() + event.getAmount());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (((CommandPvp) plugin.getCommands().get("pvp")).isPvpEnabled(event.getEntity())) {
            return;
        }
        Score score = getScore(event.getEntity());
        if (score == null || score.getScore() < DEATH_PENALTY) {
            return;
        }
        score.setScore(score.getScore() - DEATH_PENALTY);
    }

    public Score getScore(Player player) {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        if (scoreboardManager == null) {
            return null;
        }
        Objective objective = scoreboardManager.getMainScoreboard().getObjective(OBJECTIVE);
        if (objective == null) {
            return null;
        }
        return objective.getScore(player.getName());
    }

}
