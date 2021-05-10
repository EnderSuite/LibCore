package com.endersuite.libcore.strfmt.ansi;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * TODO: Add docs
 *
 * @author TheRealDomm
 * @since 08.05.21
 */
public enum Color {

    BLACK('0', "black"),
    DARK_BLUE('1', "dark_blue"),
    DARK_GREEN('2', "dark_green"),
    DARK_AQUA('3', "dark_aqua"),
    DARK_RED('4', "dark_red"),
    DARK_PURPLE('5', "dark_purple"),
    GOLD('6', "gold"),
    GRAY('7', "gray"),
    DARK_GRAY('8', "dark_gray"),
    BLUE('9', "blue"),
    GREEN('a', "green"),
    AQUA('b', "aqua"),
    RED('c', "red"),
    LIGHT_PURPLE('d', "light_purple"),
    YELLOW('e', "yellow"),
    WHITE('f', "white"),
    MAGIC('k', "obfuscated"),
    STRIKETHROUGH('l', "bold"),
    BOLD('m', "strikethrough"),
    UNDERLINED('n', "underlined"),
    ITALIC('o', "italic"),
    RESET('r', "reset");

    public static final char COLOR_CHAR = '§';
    public static final String COLOR_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr";
    public static final Pattern COLOR_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-FK-OR]");
    public static final Map<Character ,Color> CHARACTER_COLOR_MAP = new HashMap<>();

    static {
        for (Color value : values()) {
            CHARACTER_COLOR_MAP.put(value.code, value);
        }
    }

    private final char code;
    private final String string;
    private final String name;

    Color(char code, String name) {
        this.code = code;
        this.name = name;
        this.string = new String(new char[]{COLOR_CHAR, code});
    }

    public static String color(String string) {
        if (string == null) {
            return null;
        }
        return COLOR_PATTERN.matcher(string).replaceAll("");
    }

    public static String translate(char alternate, String string) {
        char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == alternate && COLOR_CODES.indexOf(chars[i+1]) > -1) {
                chars[i] = COLOR_CHAR;
                chars[i+1] = Character.toLowerCase(chars[i+1]);
            }
        }
        return new String(chars);
    }

    public static Color getCode(char code) {
        return CHARACTER_COLOR_MAP.get(code);
    }

    @Override
    public String toString() {
        return this.string;
    }
}
