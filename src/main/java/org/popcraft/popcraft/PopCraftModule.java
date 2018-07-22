package org.popcraft.popcraft;

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
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.units.JustNow;
import org.popcraft.popcraft.utils.Cooldown;
import org.popcraft.popcraft.utils.FlagTrie;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import static org.bukkit.Material.*;

public class PopCraftModule extends AbstractModule {

    public static final String FLAG_TRIE_KEY = "flag_trie";
    public static final String COMMAND_TRIE_KEY = "command_trie";

    private static final String COMMAND_NAME = "commands";
    private static final String LISTENERS_NAME = "listeners";
    private static final String RECIPES_NAME = "recipes";

    public static final Key<Map<String, CommandExecutor>> COMMAND_KEY = Key.get(
            new TypeLiteral<Map<String, CommandExecutor>>() {
            },
            Names.named(COMMAND_NAME)
    );

    public static final Key<Set<Listener>> LISTENERS_KEY = Key.get(
            new TypeLiteral<Set<Listener>>() {
            },
            Names.named(LISTENERS_NAME)
    );

    public static final Key<Set<Recipe>> RECIPES_KEY = Key.get(
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
        this.bind(Server.class).toInstance(this.plugin.getServer());
        this.bind(FileConfiguration.class).toInstance(this.plugin.getConfig());
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
                .filter(element -> !commandListenerClasses.contains(element))
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

    @Provides
    public Cooldown provideCooldown() {
        return new Cooldown(5000);
    }

    private ShapedRecipe getShapedRecipe(final Material material) {
        return new ShapedRecipe(
                new NamespacedKey(this.plugin, material.toString().toLowerCase()),
                new ItemStack(material)
        );
    }

    @Named(FLAG_TRIE_KEY)
    @Provides
    @Singleton
    public FlagTrie providesFlagTrie(final FileConfiguration configuration) {
        return this.createTrieFromString(configuration.getString("jonslogger.flag"));
    }

    @Named(COMMAND_TRIE_KEY)
    @Provides
    @Singleton
    public FlagTrie providesCommandTrie(final FileConfiguration configuration) {
        return this.createTrieFromString(configuration.getString("jonslogger.flag"));
    }

    private FlagTrie createTrieFromString(final String csv) {
        final String[] flags = csv.split(",");
        final FlagTrie trie = new FlagTrie();
        Arrays.stream(flags).forEach(trie::addFlag);
        return trie;
    }

    public static PrettyTime createPrettyTime() {
        final PrettyTime time = new PrettyTime();
        time.removeUnit(JustNow.class);
        return time;
    }

}
