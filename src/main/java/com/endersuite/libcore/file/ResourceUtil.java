package com.endersuite.libcore.file;

import lombok.Cleanup;
import org.bukkit.plugin.Plugin;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Provides static util methods to manipulate resource files.
 *
 * @author Maximilian Vincent Heidenreich
 * @since 10.05.21
 */
public class ResourceUtil {

    /**
     * Copied a resource file to specified destination.
     * ! Note: Will override existing destination file!
     *
     * @param resourcePath
     *              The path to the resource
     * @param destination
     *              The destination of the copy
     * @param plugin
     *              The Plugin reference used to construct InputStream for the resource file
     * @throws IOException
     */
    public static void extractResource(String resourcePath, Path destination, Plugin plugin) throws IOException {
        @Cleanup InputStream inputStream = plugin.getResource(resourcePath);
        if (inputStream != null)
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);

        else throw new FileNotFoundException("Resource '" + resourcePath + "' does not exist!");
    }

}
