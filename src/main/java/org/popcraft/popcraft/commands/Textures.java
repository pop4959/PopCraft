package org.popcraft.popcraft.commands;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.PopCommand;
import org.popcraft.popcraft.utils.Message;

@PopCommand("textures")
public class Textures extends PlayerCommand {

    private static final Map<String, String> resourceMap = HashMap.of(
            "1", "http://files.popcraft.org:8080/bin/PopCraft1.zip",
            "2", "http://files.popcraft.org:8080/bin/PopCraft2.zip"
    );

    @Override
    public boolean onPlayerCommand(Player player, Command cmd, String label, String[] args) {
        String url = "http://files.popcraft.org:8080/bin/PopCraft.zip";
        if (args.length == 1) {
            url = resourceMap.getOrElse(args[0], url);
        } else if (args.length > 1) {
            return false;
        }
        try {
            player.setResourcePack(url);
        } catch (NullPointerException e) {
            Message.error(player, "Cannot find texture pack. Please contact an administrator for assistance.");
        }
        return true;
    }

}
