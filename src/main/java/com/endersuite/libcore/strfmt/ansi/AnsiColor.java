package com.endersuite.libcore.strfmt.ansi;

import java.util.EnumMap;
import java.util.Map;

/**
 * TODO: Add docs
 *
 * @author TheRealDomm
 * @since 08.05.21
 */
public class AnsiColor {

    private static final Map<Color, String> COLOR_STRING_MAP = new EnumMap<Color, String>(Color.class) {{
        put(Color.BLACK, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLACK).boldOff().toString());
        put(Color.DARK_BLUE, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLUE).boldOff().toString());
        put(Color.DARK_GREEN, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.GREEN).boldOff().toString());
        put(Color.DARK_AQUA, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.CYAN).bold().toString());
        put(Color.DARK_RED, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.RED).boldOff().toString());
        put(Color.DARK_PURPLE, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.MAGENTA).boldOff().toString());
        put(Color.GOLD, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.YELLOW).boldOff().toString());
        put(Color.GRAY, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.WHITE).boldOff().toString());
        put(Color.DARK_GRAY, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLACK).bold().toString());
        put(Color.BLUE, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLUE).bold().toString());
        put(Color.GREEN, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.GREEN).bold().toString());
        put(Color.AQUA, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.CYAN).bold().toString());
        put(Color.RED, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.RED).bold().toString());
        put(Color.LIGHT_PURPLE, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.MAGENTA).bold().toString());
        put(Color.YELLOW, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.YELLOW).bold().toString());
        put(Color.WHITE, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.WHITE).bold().toString());
        put(Color.MAGIC, Ansi.ansi().a(Ansi.Attribute.BLINK_SLOW).toString());
        put(Color.BOLD, Ansi.ansi().a(Ansi.Attribute.UNDERLINE_DOUBLE).toString());
        put(Color.STRIKETHROUGH, Ansi.ansi().a(Ansi.Attribute.STRIKETHROUGH_ON).toString());
        put(Color.UNDERLINED, Ansi.ansi().a(Ansi.Attribute.UNDERLINE).toString());
        put(Color.ITALIC, Ansi.ansi().a(Ansi.Attribute.ITALIC).toString());
        put(Color.RESET, Ansi.ansi().a(Ansi.Attribute.RESET).toString());
    }};

    private static final Color[] COLORS = Color.values();


    public static String convert(String message) {
        for (Color color : COLORS)
            message = message.replaceAll("(?i)" + color.toString(), COLOR_STRING_MAP.get(color));
        return message;
    }

}
