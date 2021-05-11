package com.endersuite.libcore.inject.impl;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * @author TheRealDomm
 * @since 10.05.2021
 */
public interface Injector {

    boolean inject(File target, boolean stopOnError);

    boolean download(InputStream urlTextStream, File target, boolean keepExisting);

    boolean download(File urlFile, File target, boolean keepExisting);

    boolean download(List<String> urls, File target, boolean keepExisting);

}
