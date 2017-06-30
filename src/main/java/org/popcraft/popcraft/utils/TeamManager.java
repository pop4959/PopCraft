package org.popcraft.popcraft.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TeamManager {

    public static void assignTeam(Player player) {
	if (player.hasPermission("popcraft.tab.owner")) {
	    Bukkit.getScoreboardManager().getMainScoreboard().getTeam("0_0").addEntry(player.getName());
	} else if (player.hasPermission("popcraft.tab.admin")) {
	    Bukkit.getScoreboardManager().getMainScoreboard().getTeam("0_1").addEntry(player.getName());
	} else if (player.hasPermission("popcraft.tab.moderator")) {
	    Bukkit.getScoreboardManager().getMainScoreboard().getTeam("0_2").addEntry(player.getName());
	} else if (player.hasPermission("popcraft.tab.donator")) {
	    Bukkit.getScoreboardManager().getMainScoreboard().getTeam("6").addEntry(player.getName());
	} else if (player.hasPermission("popcraft.tab.other")) {
	    return;
	} else {
	    Bukkit.getScoreboardManager().getMainScoreboard().getTeam("f").addEntry(player.getName());
	}
    }
}
