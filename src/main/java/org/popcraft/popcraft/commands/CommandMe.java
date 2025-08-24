package org.popcraft.popcraft.commands;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandMe extends PopCraftCommand {

    public CommandMe() {
        super("me");
    }

    @Override
    public Result execute(CommandSender sender, Command command, String label, String[] args) {
        Essentials essentials = plugin.getEssentials();
        if (essentials != null && sender instanceof Player) {
            final User user = essentials.getUser((Player) sender);
            if (user != null && user.isMuted()) {
                user.sendTl("voiceSilenced");
                return Result.SUCCESS;
            }
        }
        if (args.length < 1) {
            return Result.INCORRECT_USAGE;
        }
        String message = ChatColor.translateAlternateColorCodes('&', String.join(" ", args));
        plugin.getServer().broadcastMessage(plugin.getMessage("me", sender.getName(), message));
        return Result.SUCCESS;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

}
