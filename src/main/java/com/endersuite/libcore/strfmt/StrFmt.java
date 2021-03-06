package com.endersuite.libcore.strfmt;

import com.endersuite.libcore.strfmt.ansi.AnsiColor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.logging.Logger;

/**
 * Utility for constructing beautiful strings and sending them to the Bukkit console / players.
 * It also provides a convenient API to retrieve localized strings from a configuration file.
 *      See: {@link StrFmt#fromLocalized(String)} / {@link StrFmt#fromLocalized(Configuration, String)}
 *
 * Current supported placeholders:
 *      {level}             - The level of the StrFmt
 *      {status}            - The status of the StrFmt
 *      {prefix}            - The global prefix
 *      {player.dName}      - The players display name
 *      {player.name}       - The players name
 *
 * @author Maximilian Vincent Heidenreich
 * @since 08.05.21
 */
public class StrFmt {

    // ======================   VARS

    /**
     * Global logger to use. This makes it possible for Bukkit & Non-Bukkit projects to use this class.
     */
    public static Logger logger;

    /**
     * Global prefix used in every StrFmt instance. Should be set at plugin start!
     */
    public static String prefix = "";

    /**
     * Global output level used in every StrFmt instance. Any output call with level lower than this will be voided.
     */
    public static Level outputLevel = Level.INFO;

    /**
     * Global localization config that can be set to be used as the default in every StrFmt.
     */
    public static Configuration localizationConfig;

    /**
     * The original raw string passed into the constructor.
     */
    @Getter
    private final String rawString;

    /**
     * Temporary storage of the current formatted string.
     */
    private String outputString;

    /**
     * A throwable who's stacktrace will be printed to console if not null.
     */
    private Throwable outputThrowable;

    /**
     * The status which can be displayed inside strings using the {status} placeholder.
     */
    @Getter
    private Status status;

    /**
     * The level used to decide if a message is outputted. Also used by the {level} placeholder.
     */
    @Getter
    private Level level;


    // ======================   CONSTRUCTOR

    /**
     * Creates a new string formatter using the default level (INFO) and status (HIDDEN).
     *
     * @param input
     *          The raw input string
     */
    public StrFmt(String input) {
        this(input, Level.INFO, Status.HIDDEN);
    }

    /**
     * Creates a new string formatter using the default level (INFO) and status (HIDDEN) with a throwable
     * who's stacktrace will be printed to the console when calling {@link StrFmt#toLog()}.
     *
     * @param input
     *          The raw input string
     * @param throwable
     *          The throwable to output
     */
    public StrFmt(String input, Throwable throwable) {
        this(input, Level.INFO, Status.HIDDEN);
        this.outputThrowable = throwable;
    }

    /**
     * Creates a new string formatter using the default status (HIDDEN).
     *
     * @param input
     *          The raw input string
     * @param level
     *          The level to use for this string formatter
     */
    public StrFmt(String input, Level level) {
        this(input, level, Status.HIDDEN);
    }

    /**
     * Creates a new string formatter using the default level (INFO).
     *
     * @param input
     *          The raw input string
     * @param status
     *          The status to use for this string formatter
     */
    public StrFmt(String input, Status status) {
        this(input, Level.INFO, status);
    }

    /**
     * Creates a new string formatter using the provided level & status.
     *
     * @param input
     *          The raw input string
     * @param level
     *          The level to use
     * @param status
     *          The status to use
     */
    public StrFmt(String input, Level level, Status status) {
        this.rawString = input;
        this.outputString = input + "??r";           // Prevent "color spill"
        this.level = level;
        this.status = status;
    }

    /**
     * Creates a new default string formatted using the provided path into the global localizationConfig as input.
     *
     * @param path
     *          The path to the raw string inside the localizationConfig
     * @return
     *          The created StrFmt instance
     */
    public static StrFmt fromLocalized(String path) {
        if (StrFmt.localizationConfig == null)
            return new StrFmt("StrFmt.localizedConfig is null! Path: " + path);
        else
            return StrFmt.fromLocalized(StrFmt.localizationConfig, path);
    }

    /**
     * Creates a new default string formatter using the provided path into the provided localizationConfig as input.
     *
     * @param localizationConfig
     *          The config to pull the input string from
     * @param path
     *          The path at which the input string is located
     * @return
     *          The create StrFmt instance
     */
    public static StrFmt fromLocalized(Configuration localizationConfig, String path) {
        String raw = localizationConfig.getString(path, "INVALID LOCAL PATH: " + path);
        return new StrFmt(raw);
    }


    // ======================   JAVA INTERNAL

    /**
     * Builds & Returns the formatted output string.
     * Note: prefix, level, status & color code formatters are applied by default!
     *
     * @return
     *          The formatted string
     */
    @Override
    public String toString() {
        fmtPrefix();
        fmtLevel();
        fmtStatus();
        fmtColorCodes();
        return this.outputString;
    }


    // ======================   BUSINESS LOGIC

    /**
     * Changes all & chars into ??.
     *
     * @return StrFmt instance for chaining possibility
     */
    public StrFmt fmtColorCodes() {
        return replaceAll("&", "??");
    }

    /**
     * Inserts the static prefix value at {prefix}.
     *
     * @return StrFmt instance for chaining possibility
     */
    public StrFmt fmtPrefix() {
        return replaceAll("\\{prefix}", StrFmt.prefix);
    }

    /**
     * Inserts the current level at {level}.
     *
     * @return StrFmt instance for chaining possibility
     */
    public StrFmt fmtLevel() {

        String levelStr;
        switch (getLevel()) {
            case TRACE: levelStr    = "??8TRACE??R"; break;
            case DEBUG: levelStr    = "??7??lDEBUG??R"; break;
            case INFO: levelStr     = "??3??lINFO??R"; break;
            case WARN: levelStr     = "??e??lWARN??R"; break;
            case ERROR: levelStr    = "??c??lERROR??R"; break;
            case FATAL: levelStr    = "??4??lFATAL??R"; break;
            default: levelStr       = "??r"; break;
        }

        return replaceAll("\\{level}", levelStr);
    }

    /**
     * Inserts the current status at {status}.
     *
     * @return StrFmt instance for chaining possibility
     */
    public StrFmt fmtStatus() {

        String statusChar;
        switch (status) {
            case PROGRESS: statusChar   = "??b??l...??R"; break;
            case GOOD: statusChar       = "??a??l?????R"; break;
            case BAD: statusChar        = "??c??l?????R"; break;
            default: statusChar         = "??r"; break;
        }

        return replaceAll("\\{status}", statusChar);

    }

    /**
     * Inserts:
     *          player->displayname     at {player.dName}
     *          player->name            at {player.name}
     *
     * @param player
     *          The player used as data source
     * @return
     *          The StrFmt instance for chaining possibility
     */
    public StrFmt fmtPlayer(Player player) {
        replaceAll("\\{player.dName}", player.getDisplayName());
        replaceAll("\\{player.name}", player.getName());
        return this;
    }


    // ======================   HELPERS

    /**
     * Wrapper for {@link String#replaceAll(String, String)} returning the StrFmt instance to enable chaining.
     *
     * @param regex
     *          The regex search
     * @param replacement
     *          The replacement for regex matches
     * @return
     *          The StrFmt instance for chaining possibility
     */
    private StrFmt replaceAll(String regex, String replacement) {
        this.outputString = this.outputString.replaceAll(regex, replacement);
        return this;
    }


    // ======================   OUTPUT HELPERS

    /**
     * Outputs the colored formatted string to configured logger.
     *
     * @return StrFmt instance for chaining possibility
     */
    public StrFmt toLog() {
        return toLog(true);
    }

    /**
     * Outputs the formatted string to the logger.
     *
     * @param color
     *          Whether to convert bukkit color codes to Ansi colors
     * @return StrFmt instance for chaining possibility
     */
    public StrFmt toLog(boolean color) {

        // RET: No logger specified!
        if (StrFmt.logger == null) {
            System.out.println("No logger set for StrFmt!");
            return this;
        }

        // RET: Log level to low!
        if (getLevel().toInt() < StrFmt.outputLevel.toInt()) return this;

        if (outputThrowable != null)
            outputThrowable.printStackTrace();

        String output = color ? AnsiColor.convert(this.toString()) : this.toString();
        StrFmt.logger.info(output);
        return this;

    }

    /**
     * Sends the formatted string to a specific player.
     *
     * @param player
     *          The message recipient
     * @return
     *          StrFmt instance for chaining possibility
     */
    public StrFmt toPlayer(Player player) {

        // RET: Log level to low!
        if (getLevel().toInt() < StrFmt.outputLevel.toInt()) return this;

        player.sendRawMessage(this.toString());
        return this;
    }

    /**
     * Broadcasts the formatted string to all online players.
     *
     * @return StrFmt instance for chaining possibility
     */
    public StrFmt toPlayerBroadcast() {

        // RET: Log level to low!
        if (getLevel().toInt() < StrFmt.outputLevel.toInt()) return this;

        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(this.toString()));
        return this;

    }

    /**
     * Broadcasts the formatted string to all players specified.
     *
     * @param players
     *          The recipients
     * @return
     *          StrFmt instance for chaining possibility
     */
    public StrFmt toPlayerBroadcast(Collection<Player> players) {

        // RET: Log level to low!
        if (getLevel().toInt() < StrFmt.outputLevel.toInt()) return this;

        players.forEach(p -> p.sendMessage(this.toString()));
        return this;

    }


    // ======================   GETTER & SETTER

    /**
     * Wrapper which sets the status and returns the StrFmt instance to enable chaining.
     *
     * @param status
     *          The new status
     * @return
     *          StrFmt instance for chaining possibility
     */
    public StrFmt setStatus(Status status) {
        this.status = status;
        return this;
    }

    /**
     * Wrapper which sets the level and returns the StrFmt instance to enable chaining.
     *
     * @param level
     *          The new level
     * @return
     *          StrFmt instance for chaining possibility
     */
    public StrFmt setLevel(Level level) {
        this.level = level;
        return this;
    }

}
