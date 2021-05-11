package com.endersuite.libcore.plugin.bootstrap;

import com.endersuite.libcore.inject.InjectorBootstrap;
import com.endersuite.libcore.inject.impl.Injector;
import com.endersuite.libcore.plugin.EnderPlugin;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author TheRealDomm
 * @since 11.05.2021
 */
public abstract class PluginBootstrap extends JavaPlugin {

    @Getter private final Injector injector;
    @Getter private EnderPlugin enderPlugin;

    public PluginBootstrap() {
        final InjectorBootstrap injectorBootstrap = new InjectorBootstrap();
        this.injector = injectorBootstrap.getInjector();
    }

    public PluginBootstrap(Class<? extends EnderPlugin> enderPlugin, List<String> urls, File downloadDir,
                           boolean keepExisting) {
        this(enderPlugin, urls, downloadDir, keepExisting, false);
    }

    public PluginBootstrap(Class<? extends EnderPlugin> enderPlugin, File urlFile, File downloadDir,
                           boolean keepExisting) {
        this(enderPlugin, urlFile, downloadDir, keepExisting, false);
    }

    public PluginBootstrap(Class<? extends EnderPlugin> enderPlugin, InputStream inputStream, File downloadDir,
                           boolean keepExisting) {
        this(enderPlugin, inputStream, downloadDir, keepExisting, false);
    }

    public PluginBootstrap(Class<? extends EnderPlugin> enderPlugin, List<String> urls, File downloadDir,
                           boolean keepExisting, boolean stopOnError) {
        this();
        this.injector.download(urls, downloadDir, keepExisting);
        this.injector.inject(downloadDir, stopOnError);
        this.loadPlugin(enderPlugin);
    }

    public PluginBootstrap(Class<? extends EnderPlugin> enderPlugin, File urlFile, File downloadDir,
                           boolean keepExisting, boolean stopOnError) {
        this();
        this.injector.download(urlFile, downloadDir, keepExisting);
        this.injector.inject(downloadDir, stopOnError);
        this.loadPlugin(enderPlugin);
    }

    public PluginBootstrap(Class<? extends EnderPlugin> enderPlugin, InputStream inputStream, File downloadDir,
                           boolean keepExisting, boolean stopOnError) {
        this();
        this.injector.download(inputStream, downloadDir, keepExisting);
        this.injector.inject(downloadDir, stopOnError);
        this.loadPlugin(enderPlugin);
    }

    public void loadPlugin(Class<? extends EnderPlugin> enderPlugin) {
        try {
            Constructor<? extends EnderPlugin> constructor = enderPlugin.getDeclaredConstructor(PluginBootstrap.class);
            this.enderPlugin = constructor.newInstance(this);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new IllegalStateException("Could not load enderplugin instance, wrong constructor?", e);
        }
    }

    @Override
    public void onLoad() {
        this.enderPlugin.onLoad();
    }

    @Override
    public void onEnable() {
        this.enderPlugin.onEnable();
    }

    @Override
    public void onDisable() {
        this.enderPlugin.onDisable();
    }
}
