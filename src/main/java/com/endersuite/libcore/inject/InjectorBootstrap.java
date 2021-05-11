package com.endersuite.libcore.inject;

import com.endersuite.libcore.inject.impl.ClassPathInjector;
import com.endersuite.libcore.inject.impl.Injector;
import com.endersuite.libcore.inject.impl.LegacyClassPathInjector;
import com.endersuite.libcore.strfmt.Level;
import com.endersuite.libcore.strfmt.StrFmt;
import lombok.Getter;

/**
 * Injector bootstrap that dynamically uses {@link LegacyClassPathInjector} or {@link ClassPathInjector}
 * depending on java runtime version.
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
        if (found == null) {
            new StrFmt("{prefix} Java version §c" + System.getProperty("java.version") + "§r is not supported!").setLevel(Level.FATAL).toConsole();
            throw new RuntimeException("Running currently unsupported java version '" + version + "'");
        }
        new StrFmt("{prefix} Java version §a" + System.getProperty("java.version") + "§r is supported!").setLevel(Level.INFO).toConsole();
        if (found.isUnsafe()) {
            new StrFmt("{prefix} Java version '§e" + found.getVersion() + "§r' is not recommended! Bugs my occur! Please use a version between §e8§r - §e11§r!")
                    .setLevel(Level.WARN).toConsole();
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

        @Getter
        private final String version;

        @Getter
        private final boolean unsafe;

        JavaVersion(String version, boolean unsafe) {
            this.version = version;
            this.unsafe = unsafe;
        }

    }

}
