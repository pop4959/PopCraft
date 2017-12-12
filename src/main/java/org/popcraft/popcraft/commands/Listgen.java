package org.popcraft.popcraft.commands;

import com.google.inject.Inject;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.popcraft.popcraft.newCode.PopCommand;
import org.popcraft.popcraft.utils.Message;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

@PopCommand("listgen")
public class Listgen implements CommandExecutor {

    private final JavaPlugin plugin;
    private final Server server;

    @Inject
    public Listgen(final JavaPlugin plugin, final Server server) {
        this.plugin = plugin;
        this.server = server;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        OfflinePlayer[] playerList = this.server.getOfflinePlayers();
        try {
            generatePlayerList(playerList);
            generatePlayerCount(playerList);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to create listgen file(s)", e);
            return true;
        }
        Message.normal(sender, "List generation complete.");
        return true;
    }

    public void generatePlayerList(OfflinePlayer[] playerList) throws IOException {
        File players = new File("players.txt");
        if (!players.exists())
            players.createNewFile();
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(players.getAbsoluteFile()))) {
            for (OfflinePlayer player : playerList) {
                writer.write(player.getName());
                writer.newLine();
            }
        } catch (final IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to write out player list file", e);
        }
    }

    public void generatePlayerCount(OfflinePlayer[] playerList) throws IOException {
        File count = new File("count.txt");
        if (!count.exists())
            count.createNewFile();
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(count.getAbsoluteFile()));) {
            writer.write(Integer.toString(playerList.length));
            writer.newLine();
            writer.close();
        } catch (final IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to write out player count file", e);
        }
    }
}