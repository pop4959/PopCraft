package org.popcraft.popcraft.newCode;

import com.google.inject.Injector;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import lombok.AllArgsConstructor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Jonny on 12/2/17.
 */
@AllArgsConstructor()
public class RegisterManager {

    private final Map<String, Class<? extends CommandExecutor>> commands;
    private final List<Recipe> recipeList;
    private final List<Listener> eventListeners;
    private final Injector injector;

    public void registerAll(final JavaPlugin plugin) {
        //Register All Commands
        this.commands.mapKeys(plugin::getCommand)
                .mapValues(this.injector::getInstance)
                .forEach(PluginCommand::setExecutor);

        //Register Enum based Commands

        //Register all Recipes
        this.recipeList.forEach(plugin.getServer()::addRecipe);

        //Register all Events
        this.eventListeners.forEach(
                listeners -> plugin.getServer().getPluginManager().registerEvents(listeners, plugin)
        );
    }

}
