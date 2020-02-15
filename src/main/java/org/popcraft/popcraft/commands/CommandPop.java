package org.popcraft.popcraft.commands;

import com.earth2me.essentials.Essentials;
import net.milkbowl.vault.chat.Chat;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CommandPop extends PopCraftCommand {

    private final UUID POP_UUID = UUID.fromString("a806bad6-4b60-47dd-8048-6a4945125726");
    private final OfflinePlayer POP = Bukkit.getOfflinePlayer(POP_UUID);

    public CommandPop() {
        super("pop");
    }

    @Override
    public Result execute(CommandSender sender, Command command, String label, String[] args) {
        String message = ChatColor.translateAlternateColorCodes('&', StringUtils.join(args, ' '));
        Essentials essentials = plugin.getEssentials();
        String name;
        if (essentials != null) {
            name = essentials.getUser(POP_UUID).getDisplayName();
        } else {
            Player popPlayer = POP.getPlayer();
            name = popPlayer == null ? POP.getName() : popPlayer.getDisplayName();
        }
        Chat chat = plugin.getChat();
        String prefix = chat == null ? "" : chat.getPlayerPrefix(
                Objects.requireNonNull(plugin.getConfig().getString("world.overworld")), POP);
        Bukkit.broadcastMessage(plugin.getMessage("chatFormat", prefix, name, message));
        return Result.SUCCESS;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

}
