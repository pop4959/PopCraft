package org.popcraft.popcraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

@Deprecated
public class GetScore implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("getscore")) {
            Scoreboard s = Bukkit.getScoreboardManager().getMainScoreboard();
            if (args.length == 1)
                for (Objective o : s.getObjectives())
                    sender.sendMessage(o.getName() + " " + o.getScore(args[0]).getScore());
            return true;
        }
        return false;
    }
}