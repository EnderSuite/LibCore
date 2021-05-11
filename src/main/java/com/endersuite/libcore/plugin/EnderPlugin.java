package com.endersuite.libcore.plugin;

import com.avaje.ebean.EbeanServer;
import com.endersuite.libcore.inject.impl.Injector;
import com.endersuite.libcore.plugin.bootstrap.PluginBootstrap;
import lombok.Getter;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author TheRealDomm
 * @since 11.05.2021
 */
public abstract class EnderPlugin implements Plugin {

    @Getter
    private final PluginBootstrap bootstrap;

    @Getter
    private final Injector injector;

    public EnderPlugin(PluginBootstrap bootstrap) {
        this.bootstrap = bootstrap;
        this.injector = bootstrap.getInjector();
    }

    @Override
    public File getDataFolder() {
        return bootstrap.getDataFolder();
    }

    @Override
    public PluginDescriptionFile getDescription() {
        return bootstrap.getDescription();
    }

    @Override
    public FileConfiguration getConfig() {
        return bootstrap.getConfig();
    }

    @Override
    public InputStream getResource(String filename) {
        return bootstrap.getResource(filename);
    }

    @Override
    public void saveConfig() {
        bootstrap.saveConfig();
    }

    @Override
    public void saveDefaultConfig() {
        bootstrap.saveDefaultConfig();
    }

    @Override
    public void saveResource(String resourcePath, boolean replace) {
        bootstrap.saveResource(resourcePath, replace);
    }

    @Override
    public void reloadConfig() {
        bootstrap.reloadConfig();
    }

    @Override
    public PluginLoader getPluginLoader() {
        return bootstrap.getPluginLoader();
    }

    @Override
    public Server getServer() {
        return bootstrap.getServer();
    }

    @Override
    public boolean isEnabled() {
        return bootstrap.isEnabled();
    }

    @Override
    public void onDisable() { }

    @Override
    public void onLoad() { }

    @Override
    public void onEnable() { }

    @Override
    public boolean isNaggable() {
        return bootstrap.isNaggable();
    }

    @Override
    public void setNaggable(boolean canNag) {
        bootstrap.setNaggable(canNag);
    }

    @Override
    public EbeanServer getDatabase() {
        return bootstrap.getDatabase();
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return bootstrap.getDefaultWorldGenerator(worldName, id);
    }

    @Override
    public Logger getLogger() {
        return bootstrap.getLogger();
    }

    @Override
    public String getName() {
        return bootstrap.getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

    public PluginCommand getCommand(String name) {
        return bootstrap.getCommand(name);
    }

}
