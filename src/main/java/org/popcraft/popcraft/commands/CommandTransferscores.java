package org.popcraft.popcraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.List;

public class CommandTransferscores extends PopCraftCommand {

    public CommandTransferscores() {
        super("transferscores");
    }

    @Override
    public Result execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1 || args.length > 2) {
            return Result.INCORRECT_USAGE;
        }
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        if (scoreboardManager == null) {
            return Result.FAILURE;
        }
        for (Objective o : scoreboardManager.getMainScoreboard().getObjectives()) {
            if (args.length == 1) {
                sender.sendMessage(plugin.getMessage("scoreListingFormat", o.getName(), o.getScore(args[0]).getScore()));
            } else {
                Score scoreFrom = o.getScore(args[0]), scoreTo = o.getScore(args[1]);
                scoreTo.setScore(scoreTo.getScore() + scoreFrom.getScore());
            }
        }
        if (args.length == 2) {
            sender.sendMessage(plugin.getMessage("scoreTransferred", args[0], args[1]));
        }
        return Result.SUCCESS;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

}
