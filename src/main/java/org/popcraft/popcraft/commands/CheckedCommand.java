package org.popcraft.popcraft.commands;

import com.google.common.collect.Range;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by Jonny on 12/4/17.
 */
public abstract class CheckedCommand implements CommandExecutor {

    private final Range<Integer> range;

    public CheckedCommand(Range<Integer> range) {
        this.range = range;
    }

    @Override
    public final boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        return range.contains(args.length) && this.onSafeCommand(commandSender, command, label, args);
    }

    public abstract boolean onSafeCommand(final CommandSender commandSender, Command command, String label, String[] args);

}
