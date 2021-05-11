package com.endersuite.libcore.inject.impl;

import com.endersuite.libcore.strfmt.Level;
import com.endersuite.libcore.strfmt.StrFmt;

import java.io.File;
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
        if (!(ClassLoader.getSystemClassLoader() instanceof URLClassLoader))
            throw new IllegalStateException("System class loader is no URLClassLoader");
    }

    @Override
    public boolean inject(File depsFolder, boolean stopOnError) {
        File[] files = depsFolder.listFiles();
        if (files == null) {
            System.err.println("Cannot inject null depsFolder!");
            return false;
        }

        for (File file : files) {
            if (!file.getAbsolutePath().toLowerCase(Locale.ROOT).endsWith(".jar")) {
                new StrFmt("{prefix} File in deps folder '§e" + file.getName() + "§r' is no jar file! Skipping it!")
                        .setLevel(Level.WARN).toConsole();
                continue;
            }

            new StrFmt("{prefix} Injecting '§e" + file.getName() + "§r'").setLevel(Level.DEBUG).toConsole();

            try {
                URL url = file.toURI().toURL();
                ClassLoader classLoader = ClassLoader.getSystemClassLoader();
                Class<?> urlClassLoader = URLClassLoader.class;
                try {
                    Method method = urlClassLoader.getDeclaredMethod("addURL", URL.class);
                    method.setAccessible(true);
                    method.invoke(classLoader, url);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    new StrFmt("{prefix} Could not inject '§e" + file.getName() + "§r': §c" + e.getMessage())
                            .setLevel(Level.ERROR).toConsole();
                    if (stopOnError) {
                        e.printStackTrace();
                        return false;
                    }
                }
            } catch (MalformedURLException e) {
                new StrFmt("{prefix} Could not inject successfully! " + e.getMessage())
                        .setLevel(Level.ERROR).toConsole();
                if (!stopOnError) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
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
