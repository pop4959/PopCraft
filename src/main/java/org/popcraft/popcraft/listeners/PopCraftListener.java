package org.popcraft.popcraft.listeners;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.popcraft.popcraft.PopCraft;

public class PopCraftListener implements Listener {

    protected PopCraft plugin;
    protected FileConfiguration config;

    public PopCraftListener() {
        this.plugin = PopCraft.getPlugin();
        this.config = this.plugin.getConfig();
    }

}
