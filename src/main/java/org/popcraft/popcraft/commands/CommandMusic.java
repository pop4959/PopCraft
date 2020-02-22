package org.popcraft.popcraft.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.utils.TabCompleteUtil;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class CommandMusic extends PopCraftCommand {

    private Map<UUID, Sound> playing = new HashMap<>();

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
        if (args.length < 1) {
            return Result.INCORRECT_USAGE;
        }
        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();
        if (args[0].equalsIgnoreCase("list")) {
            player.sendMessage(plugin.getMessage("musicList", StringUtils.join(MUSIC_DISCS, ", ")));
        } else if (args[0].equalsIgnoreCase("stop")) {
            if (playing.containsKey(playerUUID)) {
                player.stopSound(playing.get(playerUUID));
                playing.remove(playerUUID);
                player.sendMessage(plugin.getMessage("musicStop"));
            } else {
                player.sendMessage(plugin.getMessage("error", plugin.getMessage("musicNotPlaying")));
            }
        } else if (args[0].equalsIgnoreCase("play") && args.length >= 2) {
            try {
                Sound sound = Sound.valueOf("MUSIC_DISC_" + args[1].toUpperCase());
                float pitch = 1f;
                if (args.length >= 3) {
                    pitch = Float.parseFloat(args[2]);
                }
                if (playing.containsKey(playerUUID)) {
                    player.stopSound(playing.get(playerUUID));
                }
                playing.put(playerUUID, sound);
                player.playSound(player.getLocation(), sound, Float.MAX_VALUE, pitch);
                String pitchClamped = (new DecimalFormat("#.##")).format(Math.max(0.5f, Math.min(2f, pitch)));
                player.sendMessage(plugin.getMessage("musicPlaying", args[1], pitchClamped));
            } catch (IllegalArgumentException e) {
                player.sendMessage(plugin.getMessage("error", plugin.getMessage("musicInvalidDisc")));
            }
        } else {
            return Result.INCORRECT_USAGE;
        }
        return Result.SUCCESS;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return TabCompleteUtil.startsWithLastArg(Arrays.asList("list", "play", "stop"), args);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("play")) {
            return TabCompleteUtil.startsWithLastArg(MUSIC_DISCS, args);
        } else if (args.length == 3 && args[0].equalsIgnoreCase("play") && args[2].isEmpty()) {
            return Arrays.asList("0.5", "1.0", "2.0");
        }
        return Collections.emptyList();
    }

}
