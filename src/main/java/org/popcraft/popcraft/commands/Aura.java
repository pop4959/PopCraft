package org.popcraft.popcraft.commands;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.popcraft.popcraft.utils.Message;

public class Aura implements Listener, CommandExecutor {

    public static HashMap<UUID, Particle> playeraura = new HashMap<UUID, Particle>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	Player player = (Player) sender;
	if (cmd.getName().equalsIgnoreCase("aura")) {
	    if (args.length == 0) {
		if (playeraura.containsKey(player.getUniqueId())) {
		    playeraura.remove(player.getUniqueId());
		    Message.normal(player, "Cleared aura.");
		} else {
		    Message.usage(player, "aura <clear/list/type>");
		}
	    } else if (args.length == 1) {
		if (args[0].equalsIgnoreCase("clear")) {
		    if (playeraura.containsKey(player.getUniqueId())) {
			playeraura.remove(player.getUniqueId());
			Message.normal(player, "Cleared aura.");
		    } else {
			Message.error(player, "You don't have an aura enabled!");
		    }
		} else if (args[0].equalsIgnoreCase("list")) {
		    Message.normal(player, "Auras: " + ChatColor.RESET + "clouds, flames, smoke, sparks");
		} else if (args[0].equalsIgnoreCase("type")) {
		    Message.normal(player, "Auras: " + ChatColor.RESET + "clouds, flames, smoke, sparks");
		} else if (args[0].equals("sparks")) {
		    playeraura.put(player.getUniqueId(), Particle.FIREWORKS_SPARK);
		    Message.normal(player, "Aura effect set to " + ChatColor.RED + "sparks" + ChatColor.GOLD + ".");
		} else if (args[0].equals("smoke")) {
		    playeraura.put(player.getUniqueId(), Particle.SMOKE_LARGE);
		    Message.normal(player, "Aura effect set to " + ChatColor.RED + "smoke" + ChatColor.GOLD + ".");
		} else if (args[0].equals("flames")) {
		    playeraura.put(player.getUniqueId(), Particle.FLAME);
		    Message.normal(player, "Aura effect set to " + ChatColor.RED + "flames" + ChatColor.GOLD + ".");
		} else if (args[0].equals("clouds")) {
		    playeraura.put(player.getUniqueId(), Particle.CLOUD);
		    Message.normal(player, "Aura effect set to " + ChatColor.RED + "clouds" + ChatColor.GOLD + ".");
		} else {
		    Message.usage(player, "aura <clear/list/type>");
		}
	    } else {
		Message.usage(player, "aura <clear/list/type>");
	    }
	}
	return true;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
	Player player = event.getPlayer();
	if (playeraura.containsKey(player.getUniqueId())) {
	    Particle aura = playeraura.get(player.getUniqueId());
	    player.getWorld().spawnParticle(aura, player.getLocation(), 5, 0, 0, 0, 1, null);
	}
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
	Player player = event.getPlayer();
	if (playeraura.containsKey(player.getUniqueId())) {
	    playeraura.remove(player.getUniqueId());
	}
    }
}