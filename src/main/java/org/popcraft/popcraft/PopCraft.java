package org.popcraft.popcraft;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import static java.lang.String.format;
import static org.popcraft.popcraft.PopCraftModule.*;

//TODO Figure out this MagicMessage business
@Slf4j
public final class PopCraft extends JavaPlugin {

    private static Plugin plugin;
    public static FileConfiguration config;

    @Override
    public void onEnable() {
        //TODO Deprecate this
        plugin = this;
        config = getConfig();

        getLogger().info("Popcraft plugin starting up...");
        getConfig().options().copyDefaults(true);
        saveConfig();
        final Injector injector = Guice.createInjector(new PopCraftModule(this));

        //Register Commands
        getLogger().info("Registering Commands");
        final Map<String, CommandExecutor> commands = injector.getInstance(COMMAND_KEY);
        commands.map(pair -> format("Registering /%s with %s", pair._1, pair._2)).forEach(getLogger()::info);
        commands.mapKeys(this::getCommand).forEach(PluginCommand::setExecutor);

        Messages.registerCommands(this);

        //Register Listeners
        getLogger().info("Registering Listeners");
        final Set<Listener> listeners = injector.getInstance(LISTENERS_KEY);
        listeners.map(listener -> format("Registering %s as a listener", listener)).forEach(getLogger()::info);
        listeners.forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

        //Register Recipes
        getLogger().info("Registering Recipes");
        final Set<Recipe> recipes = injector.getInstance(RECIPES_KEY);
        getLogger().info(format("Registering %d recipes", recipes.size()));
        recipes.forEach(getServer()::addRecipe);

        getLogger().info("PopCraft is now completely enabled");
    }

    @Override
    public void onDisable() {
        plugin = null;
        getLogger().info("PopCraft plugin shutting down...");
    }

    @Deprecated
    public static Plugin getPlugin() {
        return plugin;
    }

}