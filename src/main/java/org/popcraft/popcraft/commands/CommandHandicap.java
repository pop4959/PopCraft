package org.popcraft.popcraft.commands;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CommandHandicap extends PopCraftCommand {

    public CommandHandicap() {
        super("handicap");
    }

    @Override
    public Result execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return Result.UNSUPPORTED_SENDER;
        }
        int hp;
        if (args.length < 1) {
            hp = 20;
        } else {
            try {
                hp = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                return Result.INCORRECT_USAGE;
            }
        }
        Player player = (Player) sender;
        if (hp < 1 || hp > 20 && !player.hasPermission("popcraft.handicap.bypass")) {
            player.sendMessage(plugin.getMessage("error", plugin.getMessage("handicapRangeError")));
        } else {
            AttributeInstance maxHealth = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH));
            maxHealth.setBaseValue(hp);
            player.setHealthScale(hp);
            player.sendMessage(plugin.getMessage("handicapSet", hp));
        }
        return Result.SUCCESS;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && args[0].isEmpty()) {
            return Arrays.asList("1", "20");
        }
        return Collections.emptyList();
    }

}
