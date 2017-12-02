package org.popcraft.popcraft.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.popcraft.popcraft.newCode.RegisterManager;
import org.reflections.Reflections;

/**
 * Created by Jonny on 12/2/17.
 */
public class PopCraftModule extends AbstractModule {

    private final JavaPlugin plugin;

    public PopCraftModule(final JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        this.bind(JavaPlugin.class).toInstance(this.plugin);
        this.bind(FileConfiguration.class).toInstance(this.plugin.getConfig());
    }

    @Provides
    @Singleton
    public RegisterManager provideRegisterManager() {
        Reflections reflections = new Reflections();
    }

}
