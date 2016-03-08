package org.popcraft.popcraft.utils;

import org.bukkit.entity.Player;
import java.util.HashMap;

public class Cooldown {

    private static HashMap<String, Long> cooldown = new HashMap<String, Long>();

    public static boolean check(Player player, String type, int cooldownMillis) {
	if (cooldown.containsKey(player.getName() + "-" + type)) {
	    if ((System.currentTimeMillis() - cooldown.get(player.getName() + "-" + type)) > cooldownMillis) {
		cooldown.put(player.getName() + "-" + type, System.currentTimeMillis());
		return true;
	    } else {
		return false;
	    }
	} else {
	    cooldown.put(player.getName() + "-" + type, System.currentTimeMillis());
	    return true;
	}
    }

    public static void reset(Player player, String type, int cooldownMillis) {
	cooldown.put(player.getName() + "-" + type, System.currentTimeMillis());
    }

    public static String getTimeRemaining(Player player, String type, int cooldownMillis) {
	if (cooldown.containsKey(player.getName() + "-" + type)) {
	    if (60000 > (cooldownMillis - (System.currentTimeMillis() - cooldown.get(player.getName() + "-" + type)))) {
		int seconds = Math.round(
			(cooldownMillis - (System.currentTimeMillis() - cooldown.get(player.getName() + "-" + type)))
				/ 1000);
		return seconds + " seconds";
	    } else {
		int minutes = Math.round(
			(cooldownMillis - (System.currentTimeMillis() - cooldown.get(player.getName() + "-" + type)))
				/ 60000);
		int seconds = Math
			.round((cooldownMillis
				- (System.currentTimeMillis() - cooldown.get(player.getName() + "-" + type))) / 1000)
			- (minutes * 60);
		if (minutes > 1) {
		    return minutes + " minutes " + seconds + " seconds";
		} else {
		    return minutes + " minute " + seconds + " seconds";
		}
	    }
	} else {
	    return "NaN";
	}
    }

}
