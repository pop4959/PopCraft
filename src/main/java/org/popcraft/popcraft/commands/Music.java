package org.popcraft.popcraft.commands;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.utils.Message;

public class Music implements CommandExecutor, TabCompleter {

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

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> options = new ArrayList<>();
		if (args.length == 1) {
			options.add("11");
			options.add("13");
			options.add("blocks");
			options.add("cat");
			options.add("chirp");
			options.add("far");
			options.add("mall");
			options.add("mellohi");
			options.add("stal");
			options.add("strad");
			options.add("wait");
			options.add("ward");
			options.add("list");
			options.add("stop");
		} else if (args.length == 2) {
			options.add("0.5");
			options.add("1.0");
			options.add("1.5");
			options.add("2.0");
		}
		List<String> finalOptions = new ArrayList<>();
		for (String option : options) {
			if (option.contains(args[args.length - 1])) {
				finalOptions.add(option);
			}
		}
		return finalOptions;
	}
}
