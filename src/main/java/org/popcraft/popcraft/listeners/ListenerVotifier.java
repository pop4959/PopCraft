package org.popcraft.popcraft.listeners;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.ScoreboardManager;
import org.popcraft.popcraft.utils.FileUtil;

import java.util.Objects;

/**
 * Custom voting scheme for the server
 */
public class ListenerVotifier extends PopCraftListener {

    @EventHandler
    public void onVotifier(VotifierEvent event) {
        if (!plugin.getConfig().getBoolean("voting.enabled")) {
            return;
        }
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        if (scoreboardManager == null) {
            return;
        }
        Objective objective = scoreboardManager.getMainScoreboard()
                .getObjective(Objects.requireNonNull(plugin.getConfig().getString("scoreboard.name")));
        if (objective == null) {
            return;
        }
        Vote vote = event.getVote();
        Score score = objective.getScore(vote.getUsername());
        score.setScore(score.getScore() + plugin.getConfig().getInt("voting.reward"));
        FileUtil.writeLine("votes.txt", vote.toString());
    }

}
