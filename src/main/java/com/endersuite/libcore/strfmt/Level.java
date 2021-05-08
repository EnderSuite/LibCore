package com.endersuite.libcore.strfmt;

public enum Level {
    TRACE("trace", 100),
    DEBUG("debug", 200),
    INFO("info", 300),
    WARN("warn", 400),
    ERROR("error", 500),
    FATAL("fatal", 600);

    private final String level;
    private final int level_int;

    Level(String level, int level_int) {
        this.level = level;
        this.level_int = level_int;
    }

    public int toInt() {
        return this.level_int;
    }

    @Override
    public String toString() {
        return this.level;
    }

}
