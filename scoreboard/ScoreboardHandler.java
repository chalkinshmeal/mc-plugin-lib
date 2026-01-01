package chalkinshmeal.mc_plugin_lib.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import static chalkinshmeal.mc_plugin_lib.logging.LoggerUtils.warn;
import static chalkinshmeal.mc_plugin_lib.strings.StringUtils.componentToString;
import static chalkinshmeal.mc_plugin_lib.strings.StringUtils.stringToComponent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

//-----------------------------------------------------------------------------
// ScoreboardHandler class
// A wrapper around Spigot API's Scoreboard class.
// This assumes a entry (String) -> score (Integer) mapping
//-----------------------------------------------------------------------------
public class ScoreboardHandler {
    private final String DEFAULT_NAME = "scoreboard";
    private final Component DEFAULT_TITLE = Component.text("Lives", NamedTextColor.GOLD);
    private final Scoreboard MAIN_SCOREBOARD = Bukkit.getScoreboardManager().getMainScoreboard();

    private final Scoreboard scoreboard;
    private final Objective objective;

    public ScoreboardHandler() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        this.scoreboard = manager.getNewScoreboard();
        this.objective = scoreboard.registerNewObjective(DEFAULT_NAME, Criteria.DUMMY, DEFAULT_TITLE);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    //-------------------------------------------------------------------------
    // Player methods
    //-------------------------------------------------------------------------
    public void showToPlayer(Player player) { player.setScoreboard(scoreboard); }
    public void hideFromPlayer(Player player) { player.setScoreboard(MAIN_SCOREBOARD); }

    //-------------------------------------------------------------------------
    // Entry methods
    //-------------------------------------------------------------------------
    public int getNumEntries() { return this.scoreboard.getEntries().size(); }
    public boolean hasEntry(String entry) { return this.scoreboard.getEntries().contains(entry); }
    public void addEntry(Component entry, int score) { this.addEntry(componentToString(entry), score); }
    public void addEntry(String entry, int score) {
        this.removeEntryIfExists(entry);
        this.objective.getScore(entry).setScore(score);
    }

    public void removeEntryIfExists(String entry) {
        if (!this.hasEntry(entry)) return;
        this.scoreboard.resetScores(entry);
    }

    //-------------------------------------------------------------------------
    // Score methods
    //-------------------------------------------------------------------------
    public void subtractScore(String entry, int score) { this.addScore(entry, -1 * score); }
    public void addScore(String entry, int score) { this.setScore(entry, this.getScore(entry) + score); }
    public int getScore(String entry) {
        if (!this.hasEntry(entry)) throw new IllegalArgumentException("Entry does not exist: " + entry);
        return this.objective.getScore(entry).getScore();

    }
    public void setScore(String entry, int score) {
        if (!this.hasEntry(entry)) { warn("Entry does not exist: " + entry); return; }
        this.objective.getScore(entry).setScore(score);
    }

    //-------------------------------------------------------------------------
    // Scoreboard methods
    //-------------------------------------------------------------------------
    public void setTitle(String title) { this.setTitle(stringToComponent(title)); }
    public void setTitle(Component title) { this.objective.displayName(title); }
    public void destroy() {
        for (Objective obj : this.scoreboard.getObjectives()) {
            obj.unregister();
        }
    }
}