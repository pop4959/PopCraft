package org.popcraft.popcraft.commands;

import com.google.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.popcraft.popcraft.PopCommand;
import org.popcraft.popcraft.utils.Message;

@PopCommand("magicmessage")
public class MagicMessage extends PlayerCommand implements Runnable {

    private final Server server;
    private final JavaPlugin plugin;
    private final FileConfiguration config;

    private int taskId;
    private boolean enabled = false;
    private String message = ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "*" + ChatColor.DARK_GREEN + "] "
            + ChatColor.GREEN + "Default Message";
    @Inject
    public MagicMessage(final Server server, final JavaPlugin plugin, final FileConfiguration config) {
        this.server = server;
        this.plugin = plugin;
        this.config = config;
    }

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
        this.message = ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "*" + ChatColor.DARK_GREEN + "] "
                + ChatColor.GREEN + message;
    }

    public void run() {
        for (Player player : this.server.getOnlinePlayers()) {
            if (player.hasPermission("popcraft.magicmessage.receive")) {
                player.sendMessage(message);
            }
        }
    }

    @Override
    public boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length >= 1) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("stop")) {
                    if (this.getEnabled()) {
                        Bukkit.getScheduler().cancelTask(this.getTaskId());
                        this.setEnabled(false);
                        Message.normal(player, "MagicMessage stopped.");
                    } else {
                        Message.error(player, "MagicMessage is already stopped!");
                    }
                } else if (args[0].equalsIgnoreCase("show")) {
                    Message.normal(player, "Current message: " + this.getMessage());
                } else if (args[0].equalsIgnoreCase("force")) {
                    for (Player onlinePlayer : this.server.getOnlinePlayers()) {
                        if (onlinePlayer.hasPermission("popcraft.magicmessage.receive")) {
                            Message.normal(onlinePlayer, this.getMessage());
                        }
                    }
                } else {
                    Message.usage(player, "magicmessage [start <message>/stop/show/force]");
                }
            } else {
                if (args[0].equalsIgnoreCase("start")) {
                    if (!this.getEnabled()) {
                        String message = "";
                        int n = 1;
                        while (n < args.length) {
                            message = message + args[n] + " ";
                            n++;
                        }
                        this.setMessage(message);
                        this.setEnabled(true);
                        this.setTaskId(
                                this.server.getScheduler().scheduleSyncRepeatingTask(
                                        this.plugin,
                                        this,
                                        this.config.getLong("magicmessage.defaultinterval"),
                                        this.config.getLong("magicmessage.defaultinterval")
                                )
                        );
                        Message.normal(player, "MagicMessage started...");
                    } else {
                        Message.error(player, "MagicMessage is already running!");
                    }
                }
            }
        } else {
            Message.usage(player, "magicmessage [start <message>/stop/show/force]");
        }
        return true;
    }
}