package org.popcraft.popcraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import java.util.*;

public class CommandPiggyback extends PopCraftCommand {

    private Map<UUID, Boolean> rideable = new HashMap<>();

    public CommandPiggyback() {
        super("piggyback");
    }

    @Override
    public Result execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return Result.UNSUPPORTED_SENDER;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            boolean ridingEnabled = !rideable.getOrDefault(player.getUniqueId(), false);
            rideable.put(player.getUniqueId(), ridingEnabled);
            player.sendMessage(plugin.getMessage("piggybackToggle", ridingEnabled ? "enabled" : "disabled"));
        } else {
            return Result.INCORRECT_USAGE;
        }
        return Result.SUCCESS;
    }

    public boolean isRideable(Entity entity) {
        if (entity instanceof Monster) {
            return false;
        } else if (!(entity instanceof Player)) {
            return true;
        } else {
            return rideable.getOrDefault(entity.getUniqueId(), false);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }

}
