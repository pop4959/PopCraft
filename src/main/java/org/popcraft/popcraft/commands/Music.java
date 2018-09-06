package org.popcraft.popcraft.commands;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.utils.Message;

public class Music implements CommandExecutor {

    private static HashMap<UUID, Sound> playing = new HashMap<UUID, Sound>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	Player player = (Player) sender;
	if (cmd.getName().equalsIgnoreCase("music")) {
	    try {
		if (args[0].equalsIgnoreCase("list")) {
		    Message.normal(player, "Discs: " + ChatColor.RESET
			    + "11, 13, blocks, cat, chirp, far, mall, mellohi, stal, strad, wait, ward");
		} else if (args[0].equalsIgnoreCase("stop")) {
		    if (playing.containsKey(player.getUniqueId())) {
			player.stopSound(playing.get(player.getUniqueId()));
			playing.remove(player.getUniqueId());
			Message.normal(player, "Stopped music.");
		    } else
			Message.error(player, "You are not playing anything right now!");
		} else {
		    Sound sound = Sound.valueOf("MUSIC_DISC_" + args[0].toUpperCase());
		    float pitch = 1;
		    if (args.length >= 2)
			pitch = Float.parseFloat(args[1]);
		    if (playing.containsKey(player.getUniqueId()))
			player.stopSound(playing.get(player.getUniqueId()));
		    playing.put(player.getUniqueId(), sound);
		    player.playSound(player.getLocation(), sound, Float.MAX_VALUE, pitch);
		    Message.normal(player, "Playing disc: " + ChatColor.RED + args[0].toLowerCase() + (pitch == 1 ? ""
			    : " (" + (new DecimalFormat("#.##")).format(pitch > 2 ? 2 : pitch < 0.5 ? 0.5 : pitch)
				    + "x speed)"));
		}
	    } catch (Exception e) {
		Message.usage(player, "music <disc/list/stop> [speed]");
	    }
	}
	return true;
    }
}