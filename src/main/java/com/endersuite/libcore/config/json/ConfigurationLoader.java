package com.endersuite.libcore.config.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

/**
 * @author TheRealDomm
 * @since 10.05.2021
 */
public class ConfigurationLoader {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * will save a configuration to the json file
     * @param configuration to save
     * @param file to save configuration to
     */
    public static void save(Configuration configuration, File file) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            GSON.toJson(configuration, bufferedWriter);
        } catch (IOException e) {
            throw new IllegalStateException("Could not save configuration", e);
        }
    }

    /**
     * this will update the configuration to newer version
     * @param t the class to update
     * @param file to update to
     * @param <T> the t class
     * @return the instance with updated values of t
     */
    public static <T extends Configuration> T update(Class<? extends T> t, File file) {
        try {
            T configuration = t.newInstance();
            if (file == null || !file.exists()) {
                throw new IllegalArgumentException("Can not update configuration, it does not exist");
            }
            double version = configuration.getConfigVersion();
            configuration = GSON.fromJson(new BufferedReader(new FileReader(file)), t);
            if (version > configuration.getConfigVersion()) {
                configuration.setConfigVersion(version);
                save(configuration, file);
                return configuration;
            }
            return configuration;
        } catch (IOException | IllegalAccessException | InstantiationException e) {
            throw new IllegalStateException("Could not update configuration file");
        }
    }

    /**
     * loads a configuration from the given file
     * @param t the class to load from
     * @param file to load from
     * @param <T> the t class
     * @return the instance with values of t
     */
    public static <T extends Configuration> T load(Class<? extends T> t, File file) {
        try {
            T configuration = t.newInstance();
            if (file == null) {
                throw new IllegalArgumentException("Can not create configuration from no file");
            }
            else if (!file.exists()) {
                if (!file.createNewFile()) {
                    throw new IllegalStateException("Could not create new file");
                }
                else {
                    save(configuration, file);
                }
            }
            T loaded = GSON.fromJson(new BufferedReader(new FileReader(file)), t);
            if (configuration.getConfigVersion() > loaded.getConfigVersion()) {
                return update(t, file);
            }
            return loaded;
        } catch (IOException | IllegalAccessException | InstantiationException e) {
            throw new IllegalStateException("Could not load configuration file");
        }
    }

}
