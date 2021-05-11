package com.endersuite.libcore.inject.impl;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Locale;

/**
 * @author TheRealDomm
 * @since 10.05.2021
 */
public class LegacyClassPathInjector implements Injector {

    public LegacyClassPathInjector() {
        if (!(ClassLoader.getSystemClassLoader() instanceof URLClassLoader)) {
            throw new IllegalStateException("System class loader is no URLClassLoader");
        }
    }

    @Override
    public boolean inject(File target, boolean stopOnError) {
        File[] files = target.listFiles();
        if (files == null) {
            System.err.println("No file for injection found!");
            return false;
        }
        for (File file : files) {
            if (!file.getAbsolutePath().toLowerCase(Locale.ROOT).endsWith(".jar")) {
                System.err.println("The file '" + file.getAbsolutePath() + "' is not a jar file!");
                continue;
            }
            try {
                URL url = file.toURI().toURL();
                ClassLoader classLoader = ClassLoader.getSystemClassLoader();
                Class<?> urlClassLoader = URLClassLoader.class;
                try {
                    Method method = urlClassLoader.getDeclaredMethod("addURL", URL.class);
                    method.setAccessible(true);
                    method.invoke(classLoader, url);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    System.err.println("Error while injecting '" + file.getAbsolutePath() + "'");
                    if (stopOnError) {
                        e.printStackTrace();
                        return false;
                    }
                }
            } catch (MalformedURLException e) {
                System.err.println("Could not inject all needed libraries");
                if (!stopOnError) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean download(InputStream urlTextStream, File target, boolean keepExisting) {
        return Downloader.download(urlTextStream, target, keepExisting);
    }

    @Override
    public boolean download(File urlFile, File target, boolean keepExisting) {
        return Downloader.download(urlFile, target, keepExisting);
    }

    @Override
    public boolean download(List<String> urls, File target, boolean keepExisting) {
        return Downloader.download(urls, target, keepExisting);
    }
}
