package org.popcraft.popcraft.tasks;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.popcraft.popcraft.PopCraft;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VoteHandler implements Listener {

    public static final String FILE = "./plugins/Votifier/votes.log";
    private static final Logger log = Logger.getLogger("FlatfileVoteListener");

    @EventHandler
    public void onVotifierEvent(VotifierEvent event) {
        if (PopCraft.config.getBoolean("votehandler.enabled")) {
            Vote vote = event.getVote();
            // Log the vote
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(FILE, true));
                writer.write(vote.toString());
                writer.newLine();
                writer.flush();
                writer.close();
            } catch (Exception e) {
                log.log(Level.WARNING, "Unable to log vote: " + vote);
            }
            // Distribute rewards
            if (PopCraft.config.getBoolean("votehandler.reward")) {
                XPBoard.addScore(vote.getUsername(), PopCraft.config.getInt("votehandler.amount"));
            }
        }
    }
}
