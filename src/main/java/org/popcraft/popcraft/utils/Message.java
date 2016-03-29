package org.popcraft.popcraft.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.popcraft.popcraft.utils.Cooldown;

public class Message {

    public static void normal(CommandSender sender, String message) {
	sender.sendMessage(ChatColor.GOLD + message);
    }

    public static void generic(String message) {
	System.out.print(message);
    }

    public static void whisper(CommandSender sender, String message) {
	sender.sendMessage(ChatColor.GRAY + message);
    }

    public static void error(CommandSender sender, String message) {
	sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.DARK_RED + message);
    }

    public static void usage(CommandSender sender, String message) {
	sender.sendMessage(ChatColor.RED + "Usage: /" + message);
    }

    public static void kick(Player player, String message) {
	player.kickPlayer(ChatColor.GREEN + "PopCraft\n\n" + ChatColor.WHITE + message);
    }

    public static void cooldown(Player player, String type, int cooldownMillis) {
	player.sendMessage(ChatColor.GOLD + "You can't use this command for another " + ChatColor.RED
		+ Cooldown.getTimeRemaining(player, type, cooldownMillis) + ChatColor.GOLD + ".");
    }

    public static String getCurrentTime() {
	DateFormat dateformat = new SimpleDateFormat("MM/dd/y HH:mm");
	Date date = new Date();
	return dateformat.format(date);
    }

}
