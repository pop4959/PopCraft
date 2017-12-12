package org.popcraft.popcraft.commands;

import com.google.inject.Inject;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Objective;
import org.popcraft.popcraft.newCode.PopCommand;

@PopCommand("getscore")
public class GetScore implements CommandExecutor {

    private final Server server;

    @Inject
    public GetScore(final Server server) {
        this.server = server;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 1)
            return false;
        for (Objective objective : this.server.getScoreboardManager().getMainScoreboard().getObjectives())
            sender.sendMessage(objective.getName() + " " + objective.getScore(args[0]).getScore());
        return true;
    }
}