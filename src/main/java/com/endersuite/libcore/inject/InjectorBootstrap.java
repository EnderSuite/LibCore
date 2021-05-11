package com.endersuite.libcore.inject;

import com.endersuite.libcore.inject.impl.ClassPathInjector;
import com.endersuite.libcore.inject.impl.Injector;
import com.endersuite.libcore.inject.impl.LegacyClassPathInjector;
import lombok.Getter;

/**
 * Bootstraps a project by setting up the {@link Injector} depending on the Java version.
 *
 * @author TheRealDomm
 * @since 10.05.2021
 */
public class InjectorBootstrap {

    @Getter
    private final Injector injector;

    public InjectorBootstrap() {
        String version = System.getProperty("java.version");
        JavaVersion found = null;
        for (JavaVersion value : JavaVersion.values()) {
            if (version.startsWith(value.getVersion())) {
                found = value;
                break;
            }
        }
        if (found == null)
            throw new RuntimeException("Unsupported java version '" + version + "'!");

        this.injector = found.equals(JavaVersion.JAVA_8) ? new LegacyClassPathInjector() : new ClassPathInjector();
    }

    private enum JavaVersion {
        JAVA_8("1.8"),
        JAVA_9("9.0"),
        JAVA_10("10.0"),
        JAVA_11("11.0");

        @Getter
        private final String version;

        JavaVersion(String version) {
            this.version = version;
        }

    }

}
