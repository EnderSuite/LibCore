package com.endersuite.libcore.config;

import com.endersuite.libcore.config.json.IConfiguration;
import com.endersuite.libcore.file.ResourceUtil;
import com.endersuite.libcore.strfmt.Level;
import com.endersuite.libcore.strfmt.StrFmt;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Cleanup;
import lombok.Getter;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides loading & storing functionality together with a singleton to be used throughout the project.
 * Upon loading a config that does not exist, it tries to save a resource file with matching name.
 *
 * @author Maximilian Vincent Heidenreich, TheRealDomm
 * @since 08.05.21
 */
public class ConfigManager {

    // ======================   VARS

    /**
     * The ConfigManager singleton.
     */
    private static ConfigManager instance;

    /**
     * The {@link Gson} instance used for (de)serialization.
     */
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * A map of config names and their {@link FileConfiguration} references.
     */
    @Getter
    private final Map<String, FileConfiguration> loadedFileConfigurations;

    /**
     * A map of json config names and their {@link IConfiguration} references.
     */
    @Getter
    private final Map<String, IConfiguration> loadedJsonConfigurations;


    // ======================   CONSTRUCTOR

    /**
     * Creates a new ConfigManager instance.
     * ! Note: You should probably be using {@link ConfigManager#getInstance()} !
     */
    public ConfigManager() {
        this.loadedFileConfigurations = new HashMap<>();
        this.loadedJsonConfigurations = new HashMap<>();
    }


    // ======================   BUSINESS LOGIC

    /**
     * Saves a configuration to a file.
     *
     * @param configuration
     *          The configuration to save
     * @param file
     *          The target file to write to
     */
    public void saveJson(IConfiguration configuration, File file) {
        try {
            @Cleanup BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            GSON.toJson(configuration, bufferedWriter);
        } catch (IOException e) {
            throw new IllegalStateException("Could not save json configuration to '" + file.getAbsolutePath() + "'", e);
        }
    }

    /**
     * Updates and returns a configuration if version mismatch is detected.
     *
     * @param clazz
     *          The class defining the configuration
     * @param file
     *          The configuration file
     * @param <T>
     *          The configuration type
     * @return
     *          The updated configuration
     */
    public <T extends IConfiguration> T updateJson(Class<? extends T> clazz, File file) {
        if (file == null ||!file.exists())
            throw new IllegalArgumentException("Could not update configuration, file does not exist!");

        try {
            T configuration = clazz.getDeclaredConstructor().newInstance();
            double version = configuration.getConfigVersion();
            configuration = GSON.fromJson(new BufferedReader(new FileReader(file)), clazz);
            if (version > configuration.getConfigVersion()) {
                configuration.setConfigVersion(version);
                this.saveJson(configuration, file);
                return configuration;
            }
            return configuration;
        } catch (IOException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalStateException("Could not update configuration file");
        }
    }

    /**
     * Loads a named config file from specified path.
     * If it does not exist, it will try to find a resource file with matching name.
     *
     * @param name
     *          The name of the configuration
     * @param path
     *          The path to the configuration file
     * @param plugin
     *          The {@link Plugin} used for resource extraction
     */
    public void load(String name, Path path, Plugin plugin) throws IOException {

        if (!path.toFile().exists())
            ResourceUtil.extractResource(path.getFileName().toString(), path, plugin);

        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(path.toFile());
        getLoadedFileConfigurations().put(name, fileConfiguration);

    }

    /**
     * Loads a named json config file from specified path.
     * If it does not exist, it will try to create the file.
     *
     * @param name
     *          The name of the configuration
     * @param clazz
     *          The class defining the configuration
     * @param path
     *          The path to the configuration file
     * @param <T>
     *          The configuration type
     * @throws IOException
     */
    public <T extends IConfiguration> void loadJson(String name, Class<? extends T> clazz, Path path) throws IOException {
        File configFile = path.toFile();

        if (path == null)
            throw new IllegalArgumentException("Cannot load json config from empty path!");

        try {
            T configuration = clazz.getDeclaredConstructor().newInstance();

            if (!configFile.exists()) {
                if (!configFile.createNewFile())
                    throw new IllegalStateException("Could not create new json config file at '" + path + "'");
                else
                    this.saveJson(configuration, configFile);
            }

            T loaded = GSON.fromJson(new BufferedReader(new FileReader(configFile)), clazz);

            if (configuration.getConfigVersion() > loaded.getConfigVersion()) {
                new StrFmt(String.format(
                            "{prefix} Upgrading json config at '%s' | %s -> %s",
                            path,
                            loaded.getConfigVersion(),
                            configuration.getConfigVersion()
                        ))
                        .setLevel(Level.DEBUG).toConsole();
                configuration = this.updateJson(clazz, configFile);
            }

            this.loadedJsonConfigurations.put(name, loaded);

        } catch (IOException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalStateException("Could not load json config file", e);
        }
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

    /**
     * Returns a loaded json configuration instance as defined by {@code T} or null if not found.
     *
     * @param name
     *          The name of the configuration
     * @param <T>
     *          The type of the configuration
     * @return
     *          The json configuration | {@code null} if not found
     */
    public <T extends IConfiguration> T getJson(String name) {
        return (T) getLoadedJsonConfigurations().get(name);
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
