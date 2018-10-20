package org.popcraft.popcraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.PopCraft;
import org.popcraft.popcraft.utils.Message;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class Donate implements CommandExecutor, TabCompleter {

    private static String HELP_FILE = PopCraft.getPlugin().getDataFolder().toString() + File.separatorChar + "help.txt";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("donate")) {
            if (args.length == 0) {
                Message.normal(player, "You may donate at this site: " + ChatColor.GREEN + "popcraft.enjin.com/donate");
            } else {
                String helpText;
                int page;
                try {
                    page = Integer.parseInt(args[1]);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    page = 1;
                }
                StringBuffer result = new StringBuffer(), intermediate = new StringBuffer();
                int i, line_count = 0;
                try {
                    File helpFile = new File(HELP_FILE);
                    if (!helpFile.exists()) {
                        helpFile.createNewFile();
                    }
                    FileReader helpReader = new FileReader(helpFile);
                    while ((i = helpReader.read()) != -1) {
                        char c = (char) i;
                        if (c == '\n') {
                            ++line_count;
                        }
                        if (line_count / 5 + 1 == page) {
                            intermediate.append((char) c);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int page_count = (int) Math.ceil(line_count / 5) + 1;
                if (page < 1 || page > page_count) {
                    helpText = ChatColor.DARK_RED + "Unknown page.";
                } else {
                    result.append("&e---- &6Help &e-- &6Page &c" + page + "&6/&c" + page_count + " &e----\n");
                    result.append(intermediate);
                    if (page != page_count) {
                        result.append("\n&6Type &c/" + label + " help " + (page + 1) + " &6to read the next page.");
                    }
                    helpText = ChatColor.translateAlternateColorCodes('&', result.toString());
                }
                sender.sendMessage(helpText);
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return Collections.emptyList();
    }
}
