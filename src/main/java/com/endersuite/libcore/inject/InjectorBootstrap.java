package com.endersuite.libcore.inject;

import com.endersuite.libcore.inject.impl.ClassPathInjector;
import com.endersuite.libcore.inject.impl.Injector;
import com.endersuite.libcore.inject.impl.LegacyClassPathInjector;
import lombok.Getter;

import java.util.Locale;

/**
 * @author TheRealDomm
 * @since 10.05.2021
 */
public class InjectorBootstrap {

    @Getter private final Injector injector;

    public InjectorBootstrap() {
        String version = System.getProperty("java.version");
        JavaVersion found = null;
        for (JavaVersion value : JavaVersion.values()) {
            if (version.startsWith(value.getVersion())) {
                found = value;
                break;
            }
        }
        if (found == null) {
            throw new RuntimeException("Running currently unsupported java version '" + version + "'");
        }
        System.out.println("Running injector for java '" + found.getVersion() + "'");
        if (found.isUnsafe()) {
            System.out.println("We've detected you are running java version '" + version + "', " +
                    "this version is only barely tested with our framework..., bugs may occur!");
            System.out.println("To ensure full stability with this framework please use any version from 8 to 11");
        }
        this.injector = found.equals(JavaVersion.JAVA_8) ? new LegacyClassPathInjector() : new ClassPathInjector();
    }

    private enum JavaVersion {
        JAVA_8("1.8", false),
        JAVA_9("9.0", false),
        JAVA_10("10.0", false),
        JAVA_11("11.0", false),
        JAVA_12("12.0", true),
        JAVA_13("13.0", true);

        @Getter private final String version;
        @Getter private final boolean unsafe;

        JavaVersion(String version, boolean unsafe) {
            this.version = version;
            this.unsafe = unsafe;
        }

    }

}
