package org.popcraft.popcraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.utils.Cooldown;
import org.popcraft.popcraft.utils.Message;

import java.util.function.Function;

/**
 * Created by Jonny on 12/4/17.
 */
public abstract class PlayerCommand implements CommandExecutor {

    private final Function<Player, Boolean> filter;
    private final Cooldown cooldown;

    public PlayerCommand() {
        this(null);
    }

    public PlayerCommand(final long time) {
        this(new Cooldown(time));
    }

    public PlayerCommand(final Cooldown cooldown) {
        this.filter = cooldown == null ? player -> true : cooldown;
        this.cooldown = cooldown;
    }

    @Override
    public final boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            Message.error(commandSender, "Command can only be sent by a player");
            return false;
        }
        final Player player = (Player) commandSender;
        if (!this.filter.apply(player)) {
            return true;
        }
        return this.onPlayerCommand(player, command, label, args);
    }

    public Cooldown getCooldown() {
        return cooldown;
    }

    public abstract boolean onPlayerCommand(Player player, Command command, String label, String[] args);

}
