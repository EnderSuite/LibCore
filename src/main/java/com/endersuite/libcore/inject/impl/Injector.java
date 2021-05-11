package com.endersuite.libcore.inject.impl;

import java.io.File;
import java.util.List;

/**
 * @author TheRealDomm
 * @since 10.05.2021
 */
public interface Injector {

    /**
     * Injects all files inside the specified folder into the classpath.
     *
     * @param depsFolder
     *          The folder containing the jar files
     * @param stopOnError
     *          Whether to stop injecting on error
     * @return
     *          {@code true} if all files were injected successfully | {@code false} if not
     */
    boolean inject(File depsFolder, boolean stopOnError);

    /**
     * Downloads dependencies from url inside a file (newline separated).
     *
     * @param urlFile
     *          The file inside which the urls are stored
     * @param depsFolder
     *          THe folder in which to save the downloaded files
     * @param keepExisting
     *          Whether to overwrite existing files
     * @return
     *          {@code true} if everything succeeded | {@code false} if there was an exception
     */
    boolean download(File urlFile, File depsFolder, boolean keepExisting);

    /**
     * Downloads dependencies from url list.
     *
     * @param urls
     *          The urls at which the jar file are located
     * @param depsFolder
     *          THe folder in which to save the downloaded files
     * @param keepExisting
     *          Whether to overwrite existing files
     * @return
     *          {@code true} if everything succeeded | {@code false} if there was an exception
     */
    boolean download(List<String> urls, File depsFolder, boolean keepExisting);

}
