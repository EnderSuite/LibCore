package com.endersuite.libcore.inject.impl;

import com.endersuite.libcore.strfmt.Level;
import com.endersuite.libcore.strfmt.StrFmt;
import lombok.Cleanup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides static method which can be used to download jar files into a folder.
 *
 * @author TheRealDomm
 * @since 10.05.2021
 */
public class Downloader {

    /**
     * Downloads files from urls specified in the urlFile.
     *
     * @param urlFile
     *          The file containing the urls (newline separated)
     * @param targetFolder
     *          The folder inside which the downloaded files will be saved
     * @param keepExisting
     *          Whether to override existing files
     * @return
     *          {@code true} if everything was downloaded | {@code false} if not
     */
    public static boolean download(File urlFile, File targetFolder, boolean keepExisting) {

        // Extract urls
        List<String> urls = new ArrayList<>();
        try {
            @Cleanup BufferedReader reader = new BufferedReader(new FileReader(urlFile));
            String url;
            while ((url = reader.readLine()) != null) urls.add(url);
        } catch (IOException e) {
            System.err.println("Could not extract urls from dependency urls file!");
            return false;
        }

        return download(urls, targetFolder, keepExisting);
    }

    /**
     * Downloads files from urls specified into the target directory.
     *
     * @param urls
     *          The list containing urls to jar files
     * @param targetFolder
     *          The folder inside which the downloaded files will be saved
     * @param keepExisting
     *          Whether to override existing files
     * @return
     *          {@code true} if everything was downloaded | {@code false} if not
     */
    public static boolean download(List<String> urls, File targetFolder, boolean keepExisting) {
        if (!targetFolder.exists() && !targetFolder.mkdir()) {
            System.err.println("Could not create folder to download dependencies into!");
            return false;
        }

        try {
            for (String urlStr : urls) {
                if (urlStr.isEmpty()) continue;
                URL url = new URL(urlStr);
                String[] strings = url.getPath().split("/");
                String fileName = strings[strings.length-1];
                File targetFile = new File(targetFolder, fileName);

                if (targetFile.exists() && !keepExisting) {
                    new StrFmt("{prefix} Remote dependency '" + urlStr + "' already exists at '" + targetFile.getAbsolutePath() + "'! Skipping it!")
                            .setLevel(Level.DEBUG).toConsole();
                    continue;
                }

                // Setup connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("HEAD");
                if (!connection.getHeaderField("Content-Type").equals("application/java-archive")) {
                    new StrFmt("{prefix} Dependency source at '" + urlStr + "' is no java-archive! Skipping it!")
                            .setLevel(Level.WARN).toConsole();
                    continue;
                }

                // Perform download
                new StrFmt("{prefix} Pulling remote dependency '" + urlStr + "'").setLevel(Level.DEBUG).toConsole();
                Files.copy(url.openStream(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            System.err.println("Could not successfully download necessary dependencies!");
            return false;
        }
        return true;
    }

}
