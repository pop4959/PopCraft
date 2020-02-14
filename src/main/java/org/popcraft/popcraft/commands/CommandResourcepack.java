package org.popcraft.popcraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CommandResourcepack extends PopCraftCommand {

    public CommandResourcepack() {
        super("resourcepack");
    }

    @Override
    public Result execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return Result.UNSUPPORTED_SENDER;
        }
        Player player = (Player) sender;
        try {
            player.setResourcePack(Objects.requireNonNull(plugin.getConfig().getString("resourcepack")));
        } catch (IllegalArgumentException e) {
            player.sendMessage(plugin.getMessage("resourcepackError"));
        }
        return Result.SUCCESS;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }

}
