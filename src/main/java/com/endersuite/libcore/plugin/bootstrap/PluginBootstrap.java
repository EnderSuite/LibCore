package com.endersuite.libcore.plugin.bootstrap;

import com.endersuite.libcore.inject.InjectorBootstrap;
import com.endersuite.libcore.inject.impl.Injector;
import com.endersuite.libcore.plugin.EnderPlugin;
import com.endersuite.libcore.strfmt.Level;
import com.endersuite.libcore.strfmt.StrFmt;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.List;

/**
 * @author TheRealDomm
 * @since 11.05.2021
 */
public abstract class PluginBootstrap extends JavaPlugin {

    @Getter
    private final Injector injector;

    @Getter
    private EnderPlugin enderPlugin;

    /**
     * Can be true to indicate that a fatal error occurred -> The plugin won't be loaded / enabled / disabled.
     */
    @Getter
    private boolean panic = false;


    /**
     * Creates a new bootstrap without defaults. It's up to you to download / inject dependencies.
     *
     * @param enderPlugin
     *          The EnderPlugin class to bootstrap
     * @param prefix
     *          A prefix to be used by the {@link StrFmt} class (Can be used to visually separate log output)
     */
    public PluginBootstrap(Class<? extends EnderPlugin> enderPlugin, String prefix) {
        StrFmt.logger = Bukkit.getLogger();
        StrFmt.prefix = prefix;
        final InjectorBootstrap injectorBootstrap = new InjectorBootstrap();
        this.injector = injectorBootstrap.getInjector();
    }

    public PluginBootstrap(Class<? extends EnderPlugin> enderPlugin, List<String> urls, Path depsFolder, boolean override) {
        this(enderPlugin, "");
        this.injector.download(urls, depsFolder, override);
        this.injector.inject(depsFolder);
        this.loadPlugin(enderPlugin);
    }

    public PluginBootstrap(Class<? extends EnderPlugin> enderPlugin, File urlFile, Path depsFolder, boolean override) {
        this(enderPlugin, "");
        this.injector.download(urlFile, depsFolder, override);
        this.injector.inject(depsFolder);
        this.loadPlugin(enderPlugin);
    }

    public PluginBootstrap(Class<? extends EnderPlugin> enderPlugin, InputStream urlsInputStream, Path depsFolder, boolean override) {
        this(enderPlugin, "");
        this.injector.download(urlsInputStream, depsFolder, override);
        this.injector.inject(depsFolder);
        this.loadPlugin(enderPlugin);
    }

    protected void loadPlugin(Class<? extends EnderPlugin> enderPlugin) {
        try {
            Constructor<? extends EnderPlugin> constructor = enderPlugin.getDeclaredConstructor(PluginBootstrap.class);
            this.enderPlugin = constructor.newInstance(this);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new IllegalStateException("Could not load EnderPlugin instance, wrong constructor?", e);
        }
    }

    @Override
    public void onLoad() {
        if (!panic) this.enderPlugin.onLoad();
    }

    @Override
    public void onEnable() {
        if (!panic) this.enderPlugin.onEnable();
    }

    @Override
    public void onDisable() {
        if (!panic) this.enderPlugin.onDisable();
    }


    // ======================   HELPERS

    /*public static EnderPlugin getPlugin() {
        return PluginBootstrap.enderPlugin;
    }*/

    /**
     * Panics (Disables) the plugin. Should be used in case of a fatal exception!
     */
    public void panic(String message) {
        this.panic = true;
        new StrFmt("{prefix} §4Panic§r: " + message + "! (ノಠ益ಠ)ノ彡┻━┻").setLevel(Level.FATAL).toLog();
        Bukkit.getPluginManager().disablePlugin(this);
    }

    /**
     * Panics (Disables) the plugin & Prints stacktrace. Should be used in case of a fatal exception!
     */
    public void panic(String message, Throwable throwable) {
        throwable.printStackTrace();
        this.panic(message);
    }
}
