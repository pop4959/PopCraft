package org.popcraft.popcraft.commands;

import com.google.common.base.Joiner;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.PopCommand;
import org.popcraft.popcraft.utils.Message;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@PopCommand("music")
public class Music implements CommandExecutor {

    private static final Map<String, Sound> DISCS = Stream.of(Sound.values()).filter(item -> item.toString().startsWith("RECORD_")).collect(Collectors.toMap(item -> item.name().replace("RECORD_", "").toLowerCase(), item -> item));
    private static Map<UUID, Sound> playing = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player) || args.length < 1)
            return false;
        Player player = (Player) sender;
        if ("list".equalsIgnoreCase(args[0])) {
            Message.normal(player, "Discs: " + ChatColor.RESET + Joiner.on(", ").join(DISCS.keySet()));
        } else if ("stop".equalsIgnoreCase(args[0])) {
            if (playing.containsKey(player.getUniqueId())) {
                player.stopSound(playing.remove(player.getUniqueId()));
                Message.normal(player, "Stopped music.");
            } else
                Message.error(player, "You are not playing anything right now!");
        } else
            return playDisc(player, args);
        return true;
    }

    private boolean playDisc(Player player, String[] args) {
        if (!DISCS.containsKey(args[0].toLowerCase()))
            return false;
        Sound sound = DISCS.get(args[0].toLowerCase());
        float pitch = 1;
        boolean changedPitch = false;
        if (args.length >= 2) {
            try {
                pitch = Float.parseFloat(args[1]);
                changedPitch = true;
            } catch (NumberFormatException e) {
                Message.error(player, "Speed must be a number between 0.5 and 2.0.");
                return false;
            }
        }
        if (playing.containsKey(player.getUniqueId()))
            player.stopSound(playing.get(player.getUniqueId()));
        playing.put(player.getUniqueId(), sound);
        player.playSound(player.getLocation(), sound, Float.MAX_VALUE, pitch);
        Message.normal(player, "Playing disc: " + ChatColor.RED + args[0].toLowerCase() + (changedPitch ? ""
                : " (" + (new DecimalFormat("#.##")).format(pitch > 2 ? 2 : pitch < 0.5 ? 0.5 : pitch)
                + "x speed)"));
        return true;
    }
}