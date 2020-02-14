package org.popcraft.popcraft.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandMusic extends PopCraftCommand {

    private final List<String> MUSIC_DISCS = Arrays.stream(Material.values())
            .filter(m -> m.toString().startsWith("MUSIC_DISC_"))
            .map(m -> m.toString().replace("MUSIC_DISC_", "").toLowerCase())
            .collect(Collectors.toList());

    public CommandMusic() {
        super("music");
    }

    @Override
    public Result execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return Result.UNSUPPORTED_SENDER;
        }
        // TODO: implement
        return Result.SUCCESS;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // TODO: implement
        return null;
    }

}
