package com.endersuite.libcore.inject.impl;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TheRealDomm
 * @since 10.05.2021
 */
public class Downloader {

    public static boolean download(InputStream urlTextStream, File target, boolean keepExisting) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(urlTextStream));
        List<String> urls = new ArrayList<>();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                urls.add(line);
            }
        } catch (IOException e) {
            return false;
        }
        return download(urls, target, keepExisting);
    }

    public static boolean download(File urlFile, File target, boolean keepExisting) {
        List<String> urls = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(urlFile));
            String url;
            while ((url = reader.readLine()) != null) {
                urls.add(url);
            }
        } catch (IOException e) {
            return false;
        }
        return download(urls, target, keepExisting);
    }

    public static boolean download(List<String> urls, File target, boolean keepExisting) {
        if (!target.exists() && !target.mkdir()) {
            System.err.println("Could not create dependency folder!");
            return false;
        }
        try {
            for (String urlStr : urls) {
                if (urlStr.isEmpty()) {
                    continue;
                }
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("HEAD");
                if (!connection.getHeaderField("Content-Type").equals("application/java-archive")) {
                    System.err.println("File from url '" + urlStr + "' is not a java archive");
                    continue;
                }
                String[] strings = url.getPath().split("/");
                String fileName = strings[strings.length-1];
                File targetFile = new File(target, fileName);
                if (targetFile.exists() && !keepExisting) {
                    continue;
                }
                Files.copy(url.openStream(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            System.err.println("Could not complete download of dependencies");
            return false;
        }
        return true;
    }

}
