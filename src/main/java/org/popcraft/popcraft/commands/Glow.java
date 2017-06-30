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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Team;
import org.popcraft.popcraft.PopCraft;
import org.popcraft.popcraft.utils.Message;

public class Glow implements Listener, CommandExecutor {

    private static HashMap<UUID, Object[]> task = new HashMap<UUID, Object[]>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	final Player player = (Player) sender;
	if (cmd.getName().equalsIgnoreCase("glow")) {
	    if (args.length == 0 && player.isGlowing()) {
		disableGlow(player);
	    } else {
		disableGlow(player);
		Queue<Character> sequence = getSequenceFromArgs(args);
		final String oldteam = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName())
			.getName(),
			prefix = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName())
				.getPrefix();
		player.setPlayerListName(prefix + player.getName());
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		int id = scheduler.scheduleSyncRepeatingTask(PopCraft.getPlugin(), new Runnable() {
		    @SuppressWarnings("unchecked")
		    @Override
		    public void run() {
			Character color = ((Queue<Character>) task.get(player.getUniqueId())[2]).remove();
			((Queue<Character>) task.get(player.getUniqueId())[2]).add(color);
			Team team = Bukkit.getScoreboardManager().getMainScoreboard()
				.getTeam(oldteam + player.getUniqueId().toString().substring(0, 3) + color);
			if (team == null)
			    Bukkit.getScoreboardManager().getMainScoreboard()
				    .registerNewTeam(oldteam + player.getUniqueId().toString().substring(0, 3) + color);
			Bukkit.getScoreboardManager().getMainScoreboard()
				.getTeam(oldteam + player.getUniqueId().toString().substring(0, 3) + color)
				.setPrefix(ChatColor.getByChar(color).toString());
			Bukkit.getScoreboardManager().getMainScoreboard()
				.getTeam(oldteam + player.getUniqueId().toString().substring(0, 3) + color)
				.addEntry(player.getName());
		    }
		}, 0L, 3L);
		task.put(player.getUniqueId(), new Object[] { id, oldteam, sequence });
		player.setGlowing(true);
	    }
	    Message.normal(player, "Glowing " + ChatColor.RED + (player.isGlowing() ? "enabled" : "disabled"));
	}
	return true;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
	Player killed = e.getEntity(), killer = e.getEntity().getKiller();
	if (task.containsKey(killed.getUniqueId())) {
	    e.setDeathMessage(e.getDeathMessage().replace(killed.getName(),
		    Bukkit.getScoreboardManager().getMainScoreboard()
			    .getTeam((String) task.get(killed.getUniqueId())[1]).getPrefix() + killed.getName()
			    + ChatColor.RESET));
	}
	if (killer != null && task.containsKey(killer.getUniqueId())) {
	    e.setDeathMessage(e.getDeathMessage().replace(killer.getName(),
		    Bukkit.getScoreboardManager().getMainScoreboard()
			    .getTeam((String) task.get(killer.getUniqueId())[1]).getPrefix() + killer.getName()
			    + ChatColor.RESET));
	}
    }

    private Queue<Character> getSequenceFromArgs(String[] args) {
	String pattern = "";
	if (args.length == 0)
	    pattern += "4c6e2ab319d5";
	else
	    for (String arg : args)
		pattern += arg;
	Queue<Character> sequence = new LinkedList<Character>();
	for (char c : pattern.toCharArray())
	    if ("0123456789abcdefABCDEF".indexOf(c) > -1)
		sequence.add(Character.toLowerCase(c));
	if (sequence.size() == 0)
	    sequence.add('f');
	return sequence;
    }

    public static void disableGlow(Player player) {
	if (task.containsKey(player.getUniqueId())) {
	    Bukkit.getServer().getScheduler().cancelTask((int) task.get(player.getUniqueId())[0]);
	    Bukkit.getScoreboardManager().getMainScoreboard().getTeam((String) task.get(player.getUniqueId())[1])
		    .addEntry(player.getName());
	    task.remove(player.getUniqueId());
	}
	player.setGlowing(false);
    }
}