package com.endersuite.libcore.inject.impl;

import com.endersuite.libcore.strfmt.Level;
import com.endersuite.libcore.strfmt.StrFmt;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
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
     * Downloads files from urls specified in the urlTextStream.
     *
     * @param urlTextStream
     *          The stream containing the urls (newline separated)
     * @param targetFolder
     *          The folder inside which the downloaded files will be saved
     * @param override
     *          Whether to override existing files
     */
    public static void download(InputStream urlTextStream, Path targetFolder, boolean override) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(urlTextStream));
        List<String> urls = new ArrayList<>();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                urls.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not read urls from stream!", e);
        }
        download(urls, targetFolder, override);
    }

    /**
     * Downloads files from urls specified in the urlFile.
     *
     * @param urlFile
     *          The file containing the urls (newline separated)
     * @param targetFolder
     *          The folder inside which the downloaded files will be saved
     * @param override
     *          Whether to override existing files
     */
    public static void download(File urlFile, Path targetFolder, boolean override) {
        List<String> urls = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(urlFile));
            String url;
            while ((url = reader.readLine()) != null) {
                urls.add(url);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not read urls from file at '" + urlFile.getAbsolutePath() + "'!", e);
        }
        download(urls, targetFolder, override);
    }

    /**
     * Downloads files from urls specified into the target directory.
     *
     * @param urls
     *          The list containing urls to jar files
     * @param targetFolder
     *          The folder inside which the downloaded files will be saved
     * @param override
     *          Whether to override existing files
     */
    public static void download(List<String> urls, Path targetFolder, boolean override) {
        File target = targetFolder.toFile();

        if (!target.exists()) {
            if (!target.mkdir())
                throw new RuntimeException("Could not create download target folder!");
        }
        else if (!target.isDirectory()) {
            throw new RuntimeException("Target at '" + target.getAbsolutePath() + "' exists but is a file (Should be a directory)!");
        }

        try {
            for (String urlStr : urls) {
                if (urlStr.isEmpty()) {
                    continue;
                }
                URL url = new URL(urlStr);
                String[] strings = url.getPath().split("/");
                String fileName = strings[strings.length-1];
                File targetFile = new File(target, fileName);

                if (targetFile.exists() && !override) {
                    new StrFmt("{prefix} Remote dependency '§e" + urlStr + "§r' already downloaded to '§e" + targetFile.getAbsolutePath() + "§r'! Skipping it!")
                            .setLevel(Level.DEBUG).toLog();
                    continue;
                }

                // Setup connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("HEAD");
                if (!connection.getHeaderField("Content-Type").equals("application/java-archive")) {
                    new StrFmt("{prefix} Dependency source at '§e" + urlStr + "§r' is no java-archive! Skipping it!")
                            .setLevel(Level.WARN).toLog();
                    continue;
                }

                // Perform download
                new StrFmt("{prefix} Pulling remote dependency '§e" + urlStr + "§r'").setLevel(Level.DEBUG).toLog();
                Files.copy(url.openStream(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not successfully download all dependencies!", e);
        }
    }

}
