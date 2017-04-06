package org.popcraft.popcraft.commands;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;
import org.popcraft.popcraft.PopCraft;
import org.popcraft.popcraft.utils.Message;

public class Glow implements Listener, CommandExecutor {

    private static HashMap<UUID, Object[]> task = new HashMap<UUID, Object[]>();
    private static Queue<String> rainbow = new LinkedList<String>();

    static {
	rainbow.add("4");
	rainbow.add("c");
	rainbow.add("6");
	rainbow.add("e");
	rainbow.add("2");
	rainbow.add("a");
	rainbow.add("b");
	rainbow.add("3");
	rainbow.add("1");
	rainbow.add("9");
	rainbow.add("d");
	rainbow.add("5");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	final Player player = (Player) sender;
	if (cmd.getName().equalsIgnoreCase("glow")) {
	    if (player.isGlowing()) {
		if (task.containsKey(player.getUniqueId())) {
		    Bukkit.getServer().getScheduler().cancelTask((int) task.get(player.getUniqueId())[0]);
		    Bukkit.getScoreboardManager().getMainScoreboard()
			    .getTeam((String) task.get(player.getUniqueId())[1]).addEntry(player.getName());
		}
		player.setGlowing(false);
	    } else {
		player.setGlowing(true);
		player.setPlayerListName(
			Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName()).getPrefix()
				+ player.getName());
		Message.normal(player, "Glowing " + ChatColor.RED + (player.isGlowing() ? "enabled" : "disabled"));
		String oldteam = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName())
			.getName();
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		int id = scheduler.scheduleSyncRepeatingTask(PopCraft.getPlugin(), new Runnable() {
		    @Override
		    public void run() {
			String team = rainbow.remove();
			rainbow.add(team);
			Bukkit.getScoreboardManager().getMainScoreboard().getTeam(team).addEntry(player.getName());
		    }
		}, 0L, 3L);
		task.put(player.getUniqueId(), new Object[] { id, oldteam });
	    }
	}
	return true;
    }
}