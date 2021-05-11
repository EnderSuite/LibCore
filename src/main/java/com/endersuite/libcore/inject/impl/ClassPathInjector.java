package com.endersuite.libcore.inject.impl;

import java.io.File;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Locale;

/**
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
                System.err.println("Error while injecting '" + file.getAbsolutePath() + "'");
                System.err.println(e);
                if (stopOnError) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public boolean download(File urlFile, File target, boolean keepExisting) {
        return false;
    }

    @Override
    public boolean download(List<String> urls, File target, boolean keepExisting) {
        return false;
    }

    private void setAccessible(AccessibleObject accessibleObject, boolean accessible) {
        AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
            accessibleObject.setAccessible(accessible);
           return null;
        });
    }

}
