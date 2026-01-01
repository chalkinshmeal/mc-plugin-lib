package chalkinshmeal.mc_plugin_lib.teams;

import static chalkinshmeal.mc_plugin_lib.logging.LoggerUtils.warn;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import chalkinshmeal.mc_plugin_lib.scoreboard.ScoreboardHandler;
import net.kyori.adventure.text.Component;

//-----------------------------------------------------------------------------
// TeamHandler class
// A wrapper around Spigot API's Scoreboard class, with team and score support
//-----------------------------------------------------------------------------
public class TeamHandler {
    private final JavaPlugin plugin;
    private final ScoreboardHandler scoreboardHandler;
    private final Map<String, Team> teams = new HashMap<>();
    private final Set<Listener> listeners = Set.of(new PlayerJoinListener(), new PlayerQuitListener());

    public TeamHandler(JavaPlugin plugin, String scoreboardTitle) {
        this.plugin = plugin;
        this.scoreboardHandler = new ScoreboardHandler();
        this.scoreboardHandler.setTitle(scoreboardTitle);
    }
    
    //-------------------------------------------------------------------------
    // Team methods
    //-------------------------------------------------------------------------
    public Set<String> getTeamNames() { return this.teams.keySet(); }
    public Set<Team> getTeams() { return (Set<Team>) this.teams.values(); }
    public int getNumTeams() { return this.teams.size(); }
    public boolean hasTeam(String teamName) { return this.teams.containsKey(teamName); }

    public void addTeam(String teamName, Component displayName, int startingScore) {
        if (this.hasTeam(teamName)) throw new IllegalArgumentException("Team already exists: '" + teamName + "'");
        Team team = new Team(teamName, displayName);
        this.teams.put(teamName, team);
        this.scoreboardHandler.addEntry(team.getDisplayName(), startingScore);
    }

    public void removeTeamIfExists(String teamName) {
        if (!this.hasTeam(teamName)) { warn("Team does not exist: '" + teamName + "'"); return; }
        this.teams.remove(teamName);
        this.scoreboardHandler.removeEntryIfExists(teamName);
    }

    public void removeEmptyTeams() {
        for (String teamName : this.teams.keySet()) {
            if (this.getNumPlayers(teamName) == 0) this.removeTeamIfExists(teamName);
        }
    }

    public Set<Team> getWinningTeams() {
        int maxScore = this.getMaxScore();
        Set<Team> winningTeams = Set.of();
        for (Team team : this.getTeams()) {
            if (this.getScore(team) == maxScore) winningTeams.add(team);
        }
        return winningTeams;
    }

    //-------------------------------------------------------------------------
    // Player methods
    //-------------------------------------------------------------------------
    public int getNumPlayers(String teamName) { return this.getPlayers(teamName).size(); }

    public void addPlayer(Player player, String teamName) {
        if (!this.hasTeam(teamName)) throw new IllegalArgumentException("Team does not exist: '" + teamName + "'");
        Team team = this.teams.get(teamName);
        team.addPlayer(player);
        this.scoreboardHandler.showToPlayer(player);
    }

    public void removePlayer(Player player, String teamName) {
        if (!this.hasTeam(teamName)) throw new IllegalArgumentException("Team does not exist: '" + teamName + "'");
        Team team = this.teams.get(teamName);
        team.removePlayer(player);
        this.scoreboardHandler.hideFromPlayer(player);
    }

    public Set<Player> getPlayers(String teamName) {
        if (!this.hasTeam(teamName)) throw new IllegalArgumentException("Team does not exist: '" + teamName + "'");
        return this.teams.get(teamName).getOnlinePlayers();
    }

    public Set<Player> getAllPlayers() {
        Set<Player> players = Set.of();
        for (Team team : this.getTeams()) players.addAll(team.getOnlinePlayers());
        return players;
    }

    //-------------------------------------------------------------------------
    // Score methods
    //-------------------------------------------------------------------------
    public void addScore(Team team, int delta) { this.setScore(team, this.getScore(team) + delta); }
    public void subtractScore(Team team, int delta) { this.addScore(team, -delta); }
    public void setScore(Team team, int score) {
        if (!this.hasTeam(team.getName())) throw new IllegalArgumentException("Team does not exist: '" + team.getName() + "'");
        this.scoreboardHandler.setScore(team.getName(), score);
    }

    public int getScore(Team team) { return this.getScore(team.getName()); }
    public int getScore(String teamName) {
        if (!this.hasTeam(teamName)) throw new IllegalArgumentException("Team does not exist: '" + teamName + "'");
        return this.scoreboardHandler.getScore(teamName);
    }

    public boolean noTeamHasPositiveLives() {
        for (Team team : this.getTeams()) if (this.getScore(team) > 0) return false;
        return true;
    }

    public int getMaxScore() {
        if (this.getNumTeams() <= 0) throw new IllegalArgumentException("No teams exist");
        int maxScore = Integer.MIN_VALUE;
        for (Team team : this.getTeams()) if (this.getScore(team) > maxScore) maxScore = this.getScore(team);
        return maxScore;
    }

    //-------------------------------------------------------------------------
    // TeamHandler methods
    //-------------------------------------------------------------------------
    public void hideScoreboardFromPlayers() {
        for (Team team : teams.values()) {
            for (Player player : team.getOnlinePlayers()) {
                this.scoreboardHandler.hideFromPlayer(player);
            }
        }
    }

    public void destroy() {
        this.hideScoreboardFromPlayers();
        this.scoreboardHandler.destroy();
        this.teams.clear();
    }

    //-----------------------------------------------------------------------------
    // Listener methods
    //-----------------------------------------------------------------------------
    public void registerListeners() {
		PluginManager manager = this.plugin.getServer().getPluginManager();
        for (Listener listener : this.listeners) manager.registerEvents(listener, this.plugin);
    }

    public void unregisterListeners() {
        for (Listener listener : this.listeners) HandlerList.unregisterAll(listener);
    }

    //-----------------------------------------------------------------------------
    // Listeners
    //-----------------------------------------------------------------------------
    private class PlayerJoinListener implements Listener {
        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            scoreboardHandler.showToPlayer(event.getPlayer());
        }
    }

    private class PlayerQuitListener implements Listener {
        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent event) {
            scoreboardHandler.hideFromPlayer(event.getPlayer());
        }
    }
}