package org.popcraft.popcraft.tasks;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.popcraft.popcraft.commands.PVP;

public class XPBoard implements Listener {

    private static final String OBJECTIVE_NAME = "popcraftScore";
    private static final int AMOUNT_LOST = 100;

    @EventHandler
    public void onPlayerExpChange(PlayerExpChangeEvent e) {
        if (e.getAmount() < 0) {
            return;
        }
        Objective scores = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(OBJECTIVE_NAME);
        Score score = scores.getScore(e.getPlayer().getName());
        score.setScore(score.getScore() + e.getAmount());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Objective scores = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(OBJECTIVE_NAME);
        Score score = scores.getScore(e.getEntity().getName());
        if (score.getScore() < AMOUNT_LOST || PVP.getPvp(e.getEntity())) {
            return;
        }
        score.setScore(score.getScore() - AMOUNT_LOST);
    }

}
