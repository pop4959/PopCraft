package org.popcraft.popcraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.PopCraft;
import org.popcraft.popcraft.utils.Cooldown;
import org.popcraft.popcraft.utils.Message;

public class Tpr implements CommandExecutor
{
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		final Player player = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("tpr"))
		{
			if (Cooldown.check(player, "tpr", PopCraft.config.getInt("commands.tpr.cooldown"))) {
				Bukkit.getScheduler().runTask(PopCraft.getPlugin(), new Runnable() {
		    		public void run() {
							boolean notSafe = true;
							Location randomLocation = getPseudoRandomCoordinate();
							if(player.hasPermission("popcraft.tpr.extended"))
								randomLocation = getRandomCoordinate();
							while (notSafe) {
								if (!randomLocation.getBlock().getBiome().equals(Biome.RIVER) && !randomLocation.getBlock().getBiome().equals(Biome.FROZEN_RIVER) && !randomLocation.getBlock().getBiome().equals(Biome.DEEP_OCEAN) && !randomLocation.getBlock().getBiome().equals(Biome.OCEAN) && !randomLocation.getBlock().getBiome().equals(Biome.FROZEN_OCEAN) && randomLocation.add(0, -1, 0).getBlock().getType()!=Material.STATIONARY_LAVA) {
									notSafe = false;
									player.teleport(randomLocation);
									Message.normal(player, "Teleporting to a random location...");
								}
								else {
									if(player.hasPermission("popcraft.tpr.extended"))
										randomLocation = getRandomCoordinate();
									else
										randomLocation = getPseudoRandomCoordinate();
								}
							}
						}
		    		});
				}
				else {
					Message.cooldown(player, "tpr", PopCraft.config.getInt("commands.tpr.cooldown"));
			}
			return true;
		}
		return false;
	}
	
	private Location getPseudoRandomCoordinate() {
		double xsign = Math.random();
		double zsign = Math.random();
		if (xsign > 0.5) {
			xsign = 1;
		}
		else {
			xsign = -1;
		}
		if (zsign > 0.5) {
			zsign = 1;
		}
		else {
			zsign = -1;
		}
		Location randomLocation = new Location(Bukkit.getServer().getWorld("world"), xsign * PopCraft.config.getInt("commands.tpr.range") * Math.random() + PopCraft.config.getDouble("spawn.coordinate-x"), 0, zsign * PopCraft.config.getInt("commands.tpr.range") * Math.random() + PopCraft.config.getDouble("spawn.coordinate-z"));
		if (Math.abs(randomLocation.getX()) < PopCraft.config.getInt("spawn.radius"))
			randomLocation.setX(xsign * 2 * randomLocation.getX() + PopCraft.config.getInt("spawn.radius"));
		if (Math.abs(randomLocation.getZ()) < PopCraft.config.getInt("spawn.radius"))
			randomLocation.setZ(zsign * 2 * randomLocation.getZ() + PopCraft.config.getInt("spawn.radius"));
		randomLocation.setY(Bukkit.getWorld("world").getHighestBlockYAt(randomLocation));
		randomLocation.add(0, 1, 0);
		return randomLocation;
	}
	
	private Location getRandomCoordinate() {
		double xsign = Math.random();
		double zsign = Math.random();
		if (xsign > 0.5) {
			xsign = 1;
		}
		else {
			xsign = -1;
		}
		if (zsign > 0.5) {
			zsign = 1;
		}
		else {
			zsign = -1;
		}
		Location randomLocation = new Location(Bukkit.getServer().getWorld("world"), xsign * PopCraft.config.getInt("commands.tpr.extendedrange") * Math.random() + PopCraft.config.getDouble("spawn.coordinate-x"), 0, zsign * PopCraft.config.getInt("commands.tpr.extendedrange") * Math.random() + PopCraft.config.getDouble("spawn.coordinate-z"));
		if (Math.abs(randomLocation.getX()) < PopCraft.config.getInt("spawn.radius"))
			randomLocation.setX(xsign * 2 * randomLocation.getX() + PopCraft.config.getInt("spawn.radius"));
		if (Math.abs(randomLocation.getZ()) < PopCraft.config.getInt("spawn.radius"))
			randomLocation.setZ(zsign * 2 * randomLocation.getZ() + PopCraft.config.getInt("spawn.radius"));
		randomLocation.setY(Bukkit.getWorld("world").getHighestBlockYAt(randomLocation));
		randomLocation.add(0, 1, 0);
		return randomLocation;
	}
	
}