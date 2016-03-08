package org.popcraft.popcraft.tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MagicMessage implements Runnable {
	
	private int taskId;
	private boolean enabled = false;
	private String message = ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "*" + ChatColor.DARK_GREEN + "] " + ChatColor.GREEN + "Default Message";
	
	public int getTaskId() {
		return taskId;
	}
	
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	
	public boolean getEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "*" + ChatColor.DARK_GREEN + "] " + ChatColor.GREEN + message;
	}
	
	public void run() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.hasPermission("popcraft.magicmessage.receive")) {
				player.sendMessage(message);
			}
		}
	}
	
}