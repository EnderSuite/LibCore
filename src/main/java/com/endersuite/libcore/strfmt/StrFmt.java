package com.endersuite.libcore.strfmt;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Utility for constructing beautiful strings and sending them to the Bukkit console / players.
 *
 * Current supported placeholders:
 *      {level}
 *      {status}
 *      {prefix}
 *
 */
@Log4j
public class StrFmt {

    // ======================   VARS

    /**
     * Global prefix used in every StrFmt instance. Should be set at plugin start!
     */
    public static String prefix = "";

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
     * The status which can be displayed inside strings using the {status} placeholder.
     */
    @Getter
    private Status status = Status.HIDDEN;

    /**
     * The level used to decide if a message is outputted. Also used by the {level} placeholder.
     */
    @Getter
    private Level level;


    // ======================   CONSTRUCTOR

    public StrFmt(String input) {
        this.rawString = input;
        this.outputString = input;
    }

    // ======================   JAVA INTERNAL

    @Override
    public String toString() {
        return this.outputString;
    }


    // ======================   BUSINESS LOGIC

    /**
     * Changes all & chars into §.
     *
     * @return StrFmt instance for chaining possibility
     */
    public StrFmt fmtColorCodes() {
        return replaceAll("&", "§");
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
        if (Level.TRACE.equals(this.level)) levelStr = "§8TRACE§R";
        else if (Level.DEBUG.equals(this.level)) levelStr = "§7§lDEBUG§R";
        else if (Level.INFO.equals(this.level)) levelStr = "§3§lINFO§R";
        else if (Level.WARN.equals(this.level)) levelStr = "§e§lWARN§R";
        else if (Level.ERROR.equals(this.level)) levelStr = "§c§lERROR§R";
        else if (Level.FATAL.equals(this.level)) levelStr = "§4§lFATAL§R";
        else levelStr = "";

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
            case PROGRESS: statusChar = "§b§l...§R"; break;
            case INFO: statusChar = "INFO"; break;           // TODO: Add info char (i)
            case GOOD: statusChar = "§a§l✔§R"; break;
            case BAD: statusChar = "§c§l✘§R"; break;
            case ERR: statusChar = "§c§lERROR§R"; break;
            case WARN: statusChar = "§e§lWARNING§R"; break;
            default: statusChar = ""; break;
        }

        return replaceAll("\\{status}", statusChar);

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
        this.outputString = getRawString().replaceAll(regex, replacement);
        return this;
    }


    // ======================   OUTPUT HELPERS

    /**
     * Outputs the formatted string to console.
     *
     * @return StrFmt instance for chaining possibility
     */
    public StrFmt toConsole() {

        // RET: Log level to low!
        if (log.getLevel().toInt() >= getLevel().toInt()) return this;

        Bukkit.getConsoleSender().sendMessage(this.toString());
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
        if (log.getLevel().toInt() <= getLevel().toInt())
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
        if (log.getLevel().toInt() >= getLevel().toInt()) return this;

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
        if (log.getLevel().toInt() >= getLevel().toInt()) return this;

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
