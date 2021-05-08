package com.endersuite.libcore.config;

import lombok.Cleanup;
import lombok.Getter;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides loading & storing functionality together with a singleton to be used throughout the project.
 * Upon loading a config that does not exist, it tries to save a resource file with matching name.
 *
 * @author Maximilian Vincent Heidenreich
 * @since 08.05.21
 */
public class ConfigManager {

    // ======================   VARS

    /**
     * The ConfigManager singleton.
     */
    private static ConfigManager instance;

    /**
     * A map of config names and their {@link FileConfiguration} references.
     */
    @Getter
    private final Map<String, FileConfiguration> loadedFileConfigurations;


    // ======================   CONSTRUCTOR

    /**
     * Creates a new ConfigManager instance.
     * ! Note: You should probably be using {@link ConfigManager#getInstance()} !
     */
    public ConfigManager() {
        this.loadedFileConfigurations = new HashMap<>();
    }


    // ======================   BUSINESS LOGIC

    /**
     * Loads a named config file from specified path.
     * If it does not exist, it will try to find a resource file with matching name.
     *
     * @param name
     *          The name of the configuration
     * @param path
     *          The path to the configuration file
     */
    public void load(String name, String path, Plugin plugin) throws IOException {

        Path configPath = Paths.get(path);

        // Check if exists on disk
        if (!configPath.toFile().exists()) {

            @Cleanup InputStream resourceIStream = plugin.getResource(configPath.getFileName().toString());
            if (resourceIStream != null) {
                Files.copy(resourceIStream, configPath);
            }

        }

        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(configPath.toFile());
        getLoadedFileConfigurations().put(name, fileConfiguration);

    }

    /**
     * Returns a loaded {@link Configuration} or null if not found.
     *
     * @param name
     *          The name of the configuration
     * @return
     *          The configuration | {@code null} if not found
     */
    public FileConfiguration get(String name) {
        return getLoadedFileConfigurations().get(name);
    }


    // ======================   GETTER & SETTER

    /**
     * Returns the ConfigManager singleton instance.
     * Note: Also creates one if none exists.
     *
     * @return The ConfigManager instance.
     */
    public static ConfigManager getInstance() {
        ConfigManager configManager = new ConfigManager();

        if (ConfigManager.instance == null)
            ConfigManager.instance = configManager;

        return ConfigManager.instance;
    }

}
