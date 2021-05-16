package com.endersuite.libcore.inject.impl;

import com.endersuite.libcore.strfmt.Level;
import com.endersuite.libcore.strfmt.StrFmt;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

/**
 * Provides functionality to inject dependencies into the class path and a downloads them
 * from a specified URL list if tey are not already present locally
 *
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
    public void inject(Path depsFolder) {
        File target = depsFolder.toFile();

        if (!target.exists()) {
            throw new RuntimeException("Dependency folder at '" + target.getAbsolutePath() + "' does not exist!");
        }

        File[] files = target.listFiles();
        if (files == null) {
            new StrFmt("{prefix} Tried to inject but found no dependencies to inject!").setLevel(Level.WARN).toLog();
            return;
        }

        for (File file : files) {
            if (!file.getAbsolutePath().toLowerCase(Locale.ROOT).endsWith(".jar")) {
                new StrFmt("{prefix} File in deps folder '§e" + file.getName() + "§r' is no jar file! Skipping it!")
                        .setLevel(Level.WARN).toLog();
                continue;
            }

            new StrFmt("{prefix} Injecting '§e" + file.getName() + "§r'").setLevel(Level.DEBUG).toLog();

            try {
                URL url = file.toURI().toURL();
                ClassLoader classLoader = ClassLoader.getSystemClassLoader();
                Class<?> urlClassLoader = URLClassLoader.class;
                Method method = urlClassLoader.getDeclaredMethod("addURL", URL.class);
                method.setAccessible(true);
                method.invoke(classLoader, url);
            } catch (MalformedURLException | InvocationTargetException | NoSuchMethodException |
                     IllegalAccessException e) {
                throw new RuntimeException("Exception while injecting '" + file.getName() + "'!", e);
            }
        }
    }

    @Override
    public void download(InputStream urlTextStream, Path depsFolder, boolean keepExisting) {
        Downloader.download(urlTextStream, depsFolder, keepExisting);
    }

    @Override
    public void download(File urlFile, Path depsFolder, boolean keepExisting) {
        Downloader.download(urlFile, depsFolder, keepExisting);
    }

    @Override
    public void download(List<String> urls, Path depsFolder, boolean keepExisting) {
        Downloader.download(urls, depsFolder, keepExisting);
    }

}
