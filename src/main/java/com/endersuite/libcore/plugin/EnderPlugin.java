package com.endersuite.libcore.plugin;

import com.avaje.ebean.EbeanServer;
import com.endersuite.libcore.inject.impl.Injector;
import com.endersuite.libcore.plugin.bootstrap.PluginBootstrap;
import lombok.Getter;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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

    @Getter private final JavaPlugin plugin;
    @Getter private final Injector injector;

    public EnderPlugin(PluginBootstrap plugin) {
        this.plugin = plugin;
        this.injector = plugin.getInjector();
    }

    @Override
    public File getDataFolder() {
        return this.plugin.getDataFolder();
    }

    @Override
    public PluginDescriptionFile getDescription() {
        return this.plugin.getDescription();
    }

    @Override
    public FileConfiguration getConfig() {
        return this.plugin.getConfig();
    }

    @Override
    public InputStream getResource(String filename) {
        return this.plugin.getResource(filename);
    }

    @Override
    public void saveConfig() {
        this.plugin.saveConfig();
    }

    @Override
    public void saveDefaultConfig() {
        this.plugin.saveDefaultConfig();
    }

    @Override
    public void saveResource(String resourcePath, boolean replace) {
        this.plugin.saveResource(resourcePath, replace);
    }

    @Override
    public void reloadConfig() {
        this.plugin.reloadConfig();
    }

    @Override
    public PluginLoader getPluginLoader() {
        return this.plugin.getPluginLoader();
    }

    @Override
    public Server getServer() {
        return this.plugin.getServer();
    }

    @Override
    public boolean isEnabled() {
        return this.plugin.isEnabled();
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {

    }

    @Override
    public boolean isNaggable() {
        return this.plugin.isNaggable();
    }

    @Override
    public void setNaggable(boolean canNag) {
        this.plugin.setNaggable(canNag);
    }

    @Override
    public EbeanServer getDatabase() {
        return this.plugin.getDatabase();
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return this.plugin.getDefaultWorldGenerator(worldName, id);
    }

    @Override
    public Logger getLogger() {
        return this.plugin.getLogger();
    }

    @Override
    public String getName() {
        return this.plugin.getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
