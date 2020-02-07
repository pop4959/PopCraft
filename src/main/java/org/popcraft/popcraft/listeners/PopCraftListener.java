package org.popcraft.popcraft.listeners;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.popcraft.popcraft.PopCraft;

public class PopCraftListener implements Listener {

    protected JavaPlugin plugin;
    protected FileConfiguration config;

    public PopCraftListener() {
        this.plugin = PopCraft.getPlugin();
        this.config = this.plugin.getConfig();
    }

}
