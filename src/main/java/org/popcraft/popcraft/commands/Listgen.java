package org.popcraft.popcraft.commands;

import com.google.inject.Inject;
import io.netty.handler.logging.LogLevel;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.popcraft.popcraft.newCode.PopCommand;
import org.popcraft.popcraft.utils.Message;
import org.slf4j.event.Level;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@PopCommand("listgen")
public class Listgen implements CommandExecutor {

    private final Server server;

    @Inject
    public Listgen(Server server) {
        this.server = server;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        OfflinePlayer[] playerList = this.server.getOfflinePlayers();
        try {
            generatePlayerList(playerList);
            generatePlayerCount(playerList);
        } catch (IOException e) {
            Message.error(sender, "IOException");
            return true;
        }
        Message.normal(sender, "List generation complete.");
        return true;
    }

    public void generatePlayerList(OfflinePlayer[] playerList) throws IOException {
        File players = new File("players.txt");
        if (!players.exists())
            players.createNewFile();
        BufferedWriter bw = new BufferedWriter(new FileWriter(players.getAbsoluteFile()));
        for (int i = 0; i < playerList.length; ++i) {
            bw.write(playerList[i].getName());
            bw.newLine();
        }
        bw.close();
    }

    public void generatePlayerCount(OfflinePlayer[] playerList) throws IOException {
        File count = new File("count.txt");
        if (!count.exists())
            count.createNewFile();
        BufferedWriter bw = new BufferedWriter(new FileWriter(count.getAbsoluteFile()));
        bw.write(Integer.toString(playerList.length));
        bw.newLine();
        bw.close();
    }
}