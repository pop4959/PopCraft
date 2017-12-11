package org.popcraft.popcraft.newCode;

import com.google.inject.*;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import static org.bukkit.Material.*;

public class PopCraftModule extends AbstractModule {

    private static final String COMMAND_NAME = "commands";
    private static final String LISTENERS_NAME = "listeners";
    private static final String RECIPES_NAME = "recipes";

    public static Key<Map<String, CommandExecutor>> COMMAND_KEY = Key.get(
            new TypeLiteral<Map<String, CommandExecutor>>() {
            },
            Names.named(COMMAND_NAME)
    );

    public static Key<Set<Listener>> LISTENERS_KEY = Key.get(
            new TypeLiteral<Set<Listener>>() {
            },
            Names.named(LISTENERS_NAME)
    );

    public static Key<Set<Recipe>> RECIPES_KEY = Key.get(
            new TypeLiteral<Set<Recipe>>() {
            },
            Names.named(RECIPES_NAME)
    );


    private final JavaPlugin plugin;

    public PopCraftModule(final JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        this.bind(JavaPlugin.class).toInstance(this.plugin);
        this.bind(FileConfiguration.class).toInstance(this.plugin.getConfig());
        this.bind(Server.class).toInstance(this.plugin.getServer());
    }

    @Provides
    @Singleton
    public Reflections provideReflections() {
        return new Reflections("org.popcraft.popcraft", this.plugin.getClass().getClassLoader());
    }

    @Named(COMMAND_NAME)
    @Provides
    @Singleton
    public Map<String, CommandExecutor> provideRegisterManager(
            final Injector injector,
            final Reflections reflections
    ) {
        return HashSet.ofAll(reflections.getTypesAnnotatedWith(PopCommand.class))
                .filter(clazz -> !clazz.isAnnotationPresent(Deprecated.class))
                .filter(CommandExecutor.class::isAssignableFrom)
                .toMap(
                        clazz -> clazz.getAnnotation(PopCommand.class).value(),
                        clazz -> injector.getInstance(clazz.asSubclass(CommandExecutor.class))
                );
    }

    @Named(LISTENERS_NAME)
    @Provides
    @Singleton
    public Set<Listener> provideListeners(
            final Injector injector,
            final Reflections reflections,
            @Named(COMMAND_NAME) final Map<String, CommandExecutor> commands
    ) {
        Set<Listener> commandListeners = commands.values()
                .toSet()
                .filter(Listener.class::isInstance)
                .map(Listener.class::cast);

        Set<Class<?>> commandListenerClasses = commandListeners.map(Object::getClass);

        Set<Listener> defaultListeners = HashSet.ofAll(reflections.getSubTypesOf(Listener.class))
                .filter(clazz -> !clazz.isAnnotationPresent(Deprecated.class))
                .filter(element -> !commandListenerClasses.contains(element.getClass()))
                .map(type -> injector.getInstance(type.asSubclass(Listener.class)));

        return defaultListeners.addAll(commandListeners);

    }

    @Named(RECIPES_NAME)
    @Provides
    @Singleton
    public Set<Recipe> provideRecipes() {
        return HashSet.of(
                this.getShapedRecipe(ELYTRA)
                        .shape("fcf", "fsf", "f f")
                        .setIngredient('c', CHAINMAIL_CHESTPLATE)
                        .setIngredient('f', FEATHER)
                        .setIngredient('s', NETHER_STAR),
                this.getShapedRecipe(SHULKER_SHELL)
                        .shape("ccc", "cfc", "c c")
                        .setIngredient('c', CHORUS_FRUIT)
                        .setIngredient('f', END_CRYSTAL)
        );
    }

    private ShapedRecipe getShapedRecipe(final Material material) {
        return new ShapedRecipe(
                new NamespacedKey(this.plugin, material.toString().toLowerCase()),
                new ItemStack(material)
        );
    }

}
