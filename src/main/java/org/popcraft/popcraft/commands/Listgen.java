package org.popcraft.popcraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.utils.Message;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class Listgen implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("listgen")) {
            try {
                generatePlayerList();
                generatePlayerCount();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (sender instanceof Player) {
                Message.normal((Player) sender, "List generation complete.");
            } else {
                Message.generic("List generation complete.");
            }
            return true;
        }
        return false;
    }

    public void generatePlayerList() throws IOException {
        OfflinePlayer[] playerlist = Bukkit.getServer().getOfflinePlayers();
        File players = new File("players.txt");
        if (!players.exists()) {
            players.createNewFile();
        }
        FileWriter fw = new FileWriter(players.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        int playerlistlength = playerlist.length;
        int n = 0;
        while (n < playerlistlength) {
            OfflinePlayer currentplayer = playerlist[n];
            String username = currentplayer.getName();
            bw.write(username);
            bw.newLine();
            n++;
        }
        bw.close();
    }

    public void generatePlayerCount() throws IOException {
        OfflinePlayer[] playerlist = Bukkit.getServer().getOfflinePlayers();
        File count = new File("count.txt");
        if (!count.exists()) {
            count.createNewFile();
        }
        FileWriter fw = new FileWriter(count.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        int playerlistlength = playerlist.length;
        String playercount = Integer.toString(playerlistlength);
        bw.write(playercount);
        bw.close();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return Collections.emptyList();
    }
}
