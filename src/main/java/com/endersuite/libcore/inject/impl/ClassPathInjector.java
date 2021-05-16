package com.endersuite.libcore.inject.impl;

import com.endersuite.libcore.strfmt.Level;
import com.endersuite.libcore.strfmt.StrFmt;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Locale;

/**
 * Provides functionality to inject dependencies into the class path and a downloads them
 * from a specified URL list if tey are not already present locally
 *
 * @author TheRealDomm
 * @since 10.05.2021
 */
public class ClassPathInjector implements Injector {

    /**
     *
     * IMPORTANT: Since Java 9 made major changes, it is required for Java 9+
     * to start the JVM with the following arguments to make this piece of software work properly.
     * Arguments to add: --add-opens java.base/jdk.internal.loader=ALL-UNNAMED
     * Usage would be like: java --add-opens java.base/jdk.internal.loader=ALL-UNNAMED -jar myapp.jar
     *
     */

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
                ClassLoader classLoader = ClassLoader.getSystemClassLoader();
                Field field = classLoader.getClass().getDeclaredField("ucp");
                this.setAccessible(field, true);
                Class<?> urlClassPath = Class.forName("jdk.internal.loader.URLClassPath");
                Object urlClassPathObject = field.get(classLoader);
                Method addFile = urlClassPath.getDeclaredMethod("addFile", String.class);
                this.setAccessible(addFile, true);
                addFile.invoke(urlClassPathObject, file.getAbsolutePath());
            } catch (NoSuchMethodException | NoSuchFieldException | InvocationTargetException |
                    IllegalAccessException | ClassNotFoundException e) {
                throw new RuntimeException("Exception while injecting '" + file.getName() + "'!", e);
            }
        }
    }

    @Override
    public void download(InputStream urlTextStream, Path targetFolder, boolean keepExisting) {
        Downloader.download(urlTextStream, targetFolder, keepExisting);
    }

    @Override
    public void download(File urlFile, Path targetFolder, boolean keepExisting) {
        Downloader.download(urlFile, targetFolder, keepExisting);
    }

    @Override
    public void download(List<String> urls, Path targetFolder, boolean keepExisting) {
        Downloader.download(urls, targetFolder, keepExisting);
    }


    private void setAccessible(AccessibleObject accessibleObject, boolean accessible) {
        AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
            accessibleObject.setAccessible(accessible);
           return null;
        });
    }

}
