package org.popcraft.popcraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.utils.Message;


public class Music implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		Player player = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("music"))
		{
			Material disk = Material.GOLD_RECORD;
			if (args.length == 0)
			{
			}
			else {
				if (args[0].equalsIgnoreCase("13"))
					disk = Material.GOLD_RECORD;
				else if (args[0].equalsIgnoreCase("13"))
					disk = Material.GOLD_RECORD;
				else if (args[0].equalsIgnoreCase("cat"))
					disk = Material.GREEN_RECORD;
				else if (args[0].equalsIgnoreCase("blocks"))
					disk = Material.RECORD_3;
				else if (args[0].equalsIgnoreCase("chirp"))
					disk = Material.RECORD_4;
				else if (args[0].equalsIgnoreCase("far"))
					disk = Material.RECORD_5;
				else if (args[0].equalsIgnoreCase("mall"))
					disk = Material.RECORD_6;
				else if (args[0].equalsIgnoreCase("mellohi"))
					disk = Material.RECORD_7;
				else if (args[0].equalsIgnoreCase("stal"))
					disk = Material.RECORD_8;
				else if (args[0].equalsIgnoreCase("strad"))
					disk = Material.RECORD_9;
				else if (args[0].equalsIgnoreCase("ward"))
					disk = Material.RECORD_10;
				else if (args[0].equalsIgnoreCase("11"))
					disk = Material.RECORD_11;
				else if (args[0].equalsIgnoreCase("wait"))
					disk = Material.RECORD_12;
				else
					disk = Material.GOLD_RECORD;
			}
			if (args.length == 1) {
					if (args[0].equalsIgnoreCase("list"))
					{
						Message.normal(player, "Discs: " + ChatColor.RESET + "13, cat, blocks, chirp, far, mall, mellohi, stal, strad, ward, 11, wait");
					}
					else {
						if (args[0].equalsIgnoreCase("13") || args[0].equalsIgnoreCase("cat") || args[0].equalsIgnoreCase("blocks") || args[0].equalsIgnoreCase("chirp") || args[0].equalsIgnoreCase("far") || args[0].equalsIgnoreCase("mall") || args[0].equalsIgnoreCase("mellohi") || args[0].equalsIgnoreCase("stal") || args[0].equalsIgnoreCase("strad") || args[0].equalsIgnoreCase("ward") || args[0].equalsIgnoreCase("11") || args[0].equalsIgnoreCase("wait")) {
							player.playEffect(player.getLocation(), Effect.RECORD_PLAY, disk);
							Message.normal(player, "Playing disc: " + ChatColor.RED + args[0].toLowerCase());
						}
						else {
							Message.error(player, "That music disc does not exist!");
						}
					}
			}
			else {
				Message.usage(player, "music <disc/list>");
			}
			return true;
		}
		return false;
	}
}