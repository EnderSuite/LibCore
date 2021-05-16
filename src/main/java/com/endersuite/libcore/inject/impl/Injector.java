package com.endersuite.libcore.inject.impl;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
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
     */
    void inject(Path depsFolder);

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
    void download(InputStream urlTextStream, Path targetFolder, boolean override);

    /**
     * Downloads dependencies from url inside a file (newline separated).
     *
     * @param urlFile
     *          The file inside which the urls are stored
     * @param targetFolder
     *          THe folder in which to save the downloaded files
     * @param override
     *          Whether to overwrite existing files
     */
    void download(File urlFile, Path targetFolder, boolean override);

    /**
     * Downloads dependencies from url list.
     *
     * @param urls
     *          The urls at which the jar file are located
     * @param targetFolder
     *          THe folder in which to save the downloaded files
     * @param override
     *          Whether to overwrite existing files
     */
    void download(List<String> urls, Path targetFolder, boolean override);

}
