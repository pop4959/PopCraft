package org.popcraft.popcraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.popcraft.popcraft.PopCraft;

public abstract class PopCraftCommand implements TabCompleter {

    protected PopCraft plugin;
    protected FileConfiguration config;
    protected String name;

    public PopCraftCommand(String name) {
        this.plugin = PopCraft.getPlugin();
        this.config = this.plugin.getConfig();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract Result execute(CommandSender sender, Command command, String label, String[] args);

}
