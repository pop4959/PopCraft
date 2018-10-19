package org.popcraft.popcraft.tasks;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.popcraft.popcraft.PopCraft;
import org.popcraft.popcraft.utils.Message;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class JonsLogger implements Listener, CommandExecutor {

    private String fileLocation = "flag.txt";
    private String banLocation = "ban.txt";
    private String[] chatflag;
    private final String[] commandflag;

    public JonsLogger(PopCraft plugin, String[] chatflags, String[] commandflag) {
        this.chatflag = chatflags;
        this.commandflag = commandflag;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("lol")) {
            sender.sendMessage("Word: "
                    + Arrays.toString(PopCraft.getPlugin().getConfig().getString("jonslogger.flag").split(","))
                    + "\nCommands: "
                    + Arrays.toString(PopCraft.getPlugin().getConfig().getString("jonslogger.commands").split(",")));
        }
        if (cmd.getName().equalsIgnoreCase("lolreload")) {
            ArrayList<String> command = new ArrayList<String>();
            command.add(PopCraft.getPlugin().getConfig().getString("jonslogger.file"));
            try {
                Runtime.getRuntime().exec((String[]) command.toArray(new String[1]));
                Message.normal(sender, "Reloaded Successfully.");
            } catch (Exception e) {
                e.printStackTrace();
                Message.error((Player) sender, "Failed to reload!");
            }
        }
        return true;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String name = event.getPlayer().getName();
        String s = event.getMessage();
        if (filter(s, this.chatflag)) {
            write(log(s, name), false);
        }
    }

    public boolean check(String[] temp) {
        String[] arrayOfString;
        int j = (arrayOfString = this.commandflag).length;
        for (int i = 0; i < j; i++) {
            String t = arrayOfString[i];
            if (temp[0].equals(t)) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String s = event.getMessage();
        String name = event.getPlayer().getName();
        String[] temp = s.split(" ");
        if (temp.length == 1) {
            return;
        }
        if ((check(temp)) && (filter(s, this.chatflag))) {
            write(log(s, name), false);
        }
        if ((temp[0].equalsIgnoreCase("/ban")) && (PopCraft.getPlugin().getConfig().getBoolean("jonslogger.showban"))
                && (event.getPlayer().hasPermission("essentials.ban"))) {
            String[] tokens = s.split(" ");
            if (tokens.length == 1) {
                return;
            }
            if (tokens.length == 2) {
                update(event.getMessage());
                write(log(tokens[1], "The Ban Hammer has spoken!", name), true);
            }
            if (tokens.length >= 3) {
                update(event.getMessage());
                write(log(tokens[1], s.substring(s.indexOf(tokens[2])), name), true);
            }
        }
        if ((temp[0].equalsIgnoreCase("/unban")) || (temp[0].equalsIgnoreCase("/pardon"))) {
            if ((PopCraft.getPlugin().getConfig().getBoolean("jonslogger.showban"))
                    && (event.getPlayer().hasPermission("essentials.unban"))) {
                update(event.getMessage());
            }
        }
    }

    @EventHandler
    public void onConsoleCommand(ServerCommandEvent event) {
        String s = event.getCommand();
        String[] tokens = s.split(" ");
        if ((tokens.length == 1) || (!PopCraft.getPlugin().getConfig().getBoolean("jonslogger.showban"))) {
            return;
        }
        if ((tokens.length == 2) && (tokens[0].toUpperCase().equals("ban".toUpperCase()))) {
            update(event.getCommand());
            write(log(tokens[1], "The Ban Hammer has spoken!", "Server"), true);
        }
        if ((tokens.length >= 3) && (tokens[0].toUpperCase().equals("ban".toUpperCase()))) {
            update(event.getCommand());
            write(log(tokens[1], s.substring(s.indexOf(tokens[2])), "Server"), true);
        }
        if (tokens[0].toUpperCase().equals("unban".toUpperCase())) {
            update(event.getCommand());
        }
    }

    private boolean filter(String s, String[] list) {
        String temp = s.replaceAll("\\s+", "");
        int x = 0;
        int count = 0;
        String[] arrayOfString;
        int j = (arrayOfString = list).length;
        for (int i = 0; i < j; i++) {
            String t = arrayOfString[i];
            if (temp.toUpperCase().contains(t.toUpperCase())) {
                return true;
            }
        }
        if (PopCraft.getPlugin().getConfig().getBoolean("jonslogger.checkip")) {
            do {
                x = temp.indexOf(".", x + 1);
                if ((x < 0) || (x + 1 >= temp.length())) {
                    return false;
                }
                if ((Character.isDigit(temp.charAt(x + 1))) && (Character.isDigit(temp.charAt(x - 1)))) {
                    count++;
                } else {
                    count = 0;
                }
            } while (count != 3);
            return true;
        }
        return false;
    }

    private void write(String s, boolean b) {
        ArrayList<String> entries = new ArrayList<String>();
        String tempLocation = this.fileLocation;
        if (b) {
            tempLocation = this.banLocation;
        }
        try {
            File temp = new File(tempLocation);
            int i;
            if (temp.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(temp));
                for (i = 0; i < countLines(tempLocation); i++) {
                    entries.add(br.readLine());
                }
                br.close();
            }
            entries.add(s);
            PrintWriter writer = new PrintWriter(tempLocation, "UTF-8");
            for (String a : entries) {
                writer.println(a);
            }
            writer.close();
        } catch (Exception e) {
            PopCraft.getPlugin().getLogger().info("Failed to write log");
            e.printStackTrace();
        }
    }

    private void update(String removing) {
        ArrayList<String> entries = new ArrayList<String>();
        String tempLocation = this.banLocation;
        try {
            String[] mainTokens = removing.split(" ");
            if (mainTokens.length <= 1) {
                return;
            }
            String remove = mainTokens[1];
            File temp = new File(tempLocation);
            if (temp.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(temp));
                for (int i = 0; i < countLines(tempLocation); i++) {
                    entries.add(br.readLine());
                }
                br.close();
            }
            PrintWriter writer = new PrintWriter(tempLocation, "UTF-8");
            String[] tokens;
            for (int i = 0; i < entries.size(); i++) {
                tokens = ((String) entries.get(i)).split("\\s+");
                if (tokens[0].equals(remove)) {
                    entries.remove(i);
                    i--;
                }
            }
            for (String a : entries) {
                writer.println(a);
            }
            writer.close();
        } catch (Exception e) {
            PopCraft.getPlugin().getLogger().info("Failed to write log");
            e.printStackTrace();
        }
    }

    public static int countLines(String filename) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            is.close();
        }
    }

    private String log(String message, String name) {
        return Message.getCurrentTime() + " - " + name + " - " + message;
    }

    private String log(String victim, String reason, String name) {
        return Message.getCurrentTime() + " - " + name + " - " + victim + " - " + reason;
    }

}