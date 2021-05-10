package com.endersuite.libcore.inject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author TheRealDomm
 * @since 10.05.2021
 */
public class ClassPathInjector {

    private final List<String> urls;
    private final boolean replaceExisting;

    /**
     * default constructor for dependency injection
     * @param file the file to read urls from
     * @param target the folder where libraries should be downloaded to
     */
    public ClassPathInjector(File file, File target) {
        this.urls = new ArrayList<>();
        this.replaceExisting = true;
        if (file == null || !file.exists() || target == null) {
            throw new IllegalArgumentException("File does not exist!");
        }
        if (!(ClassLoader.getSystemClassLoader() instanceof URLClassLoader)) {
            throw new IllegalStateException("System class loader is no URLClassLoader");
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String url;
            while ((url = reader.readLine()) != null) {
                urls.add(url);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not load dependency file!");
        }
        this.download(target);
    }

    public ClassPathInjector(List<String> urls, File target, boolean replaceExisting) {
        if (!(ClassLoader.getSystemClassLoader() instanceof URLClassLoader)) {
            throw new IllegalStateException("System class loader is no URLClassLoader");
        }
        this.urls = urls;
        this.replaceExisting = replaceExisting;
        this.download(target);
    }

    /**
     * this method will be called when injection is completed and can be easily overwritten
     */
    public void onComplete() {

    }

    /**
     * this will inject the dependency folder into the classpath of the current server instance
     * @param target the folder to inject
     */
    public void inject(File target) {
        File[] files = target.listFiles();
        if (files == null) {
            throw new IllegalStateException("No file for injection found!");
        }
        for (File file : files) {
            if (!file.getAbsolutePath().toLowerCase(Locale.ROOT).endsWith(".jar")) {
                continue;
            }
            try {
                URL url = file.toURI().toURL();
                Class[] parameters = new Class[]{URL.class};
                ClassLoader classLoader = ClassLoader.getSystemClassLoader();
                Class<?> urlClassLoader = URLClassLoader.class;
                try {
                    Method method = urlClassLoader.getDeclaredMethod("addURL", parameters);
                    method.setAccessible(true);
                    method.invoke(classLoader, url);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                throw new IllegalStateException("Could not inject all needed libraries", e);
            }
        }
        this.onComplete();
    }

    /**
     * this will download all dependencies needed
     * @param target the target folder to download file to
     */
    public void download(File target) {
        if (!target.exists() && !target.mkdir()) {
            throw new IllegalStateException("Could not create dependency folder!");
        }
        try {
            for (String urlStr : this.urls) {
                if (urlStr.isEmpty()) {
                    continue;
                }
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("HEAD");
                if (!connection.getHeaderField("Content-Type").equals("application/java-archive")) {
                    continue;
                }
                String[] strings = url.getPath().split("/");
                String fileName = strings[strings.length-1];
                File targetFile = new File(target, fileName);
                if (targetFile.exists() && !this.replaceExisting) {
                    continue;
                }
                Files.copy(url.openStream(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not complete download of dependencies", e);
        }
        this.inject(target);
    }

}
