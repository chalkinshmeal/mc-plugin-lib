package chalkinshmeal.mc_plugin_lib.teams;

import static chalkinshmeal.mc_plugin_lib.logging.LoggerUtils.warn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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
    private boolean isScoreboardVisible = false;

    public TeamHandler(JavaPlugin plugin, String scoreboardTitle) {
        this.plugin = plugin;
        this.scoreboardHandler = new ScoreboardHandler();
        this.scoreboardHandler.setTitle(scoreboardTitle);
    }

    //-------------------------------------------------------------------------
    // Accessor/Mutators
    //-------------------------------------------------------------------------
    public void setScoreboardVisible(boolean isVisible) { this.isScoreboardVisible = isVisible; }
    public boolean isScoreboardVisible() { return this.isScoreboardVisible; }
    
    //-------------------------------------------------------------------------
    // Team methods
    //-------------------------------------------------------------------------
    public Set<String> getTeamNames() { return this.teams.keySet(); }
    public Set<Team> getTeams() { return new HashSet<>(this.teams.values()); }
    public int getNumTeams() { return this.teams.size(); }
    public Team getTeam(String teamKey) { return this.teams.get(teamKey); }
    public boolean hasTeam(String teamKey) { return this.teams.containsKey(teamKey); }
    public boolean hasTeam(Team team) { return this.teams.containsKey(team.getKey()); }
    public boolean hasTeam(Player player) {
        for (Team team : this.getTeams()) if (team.hasPlayer(player)) return true;
        return false;
    }

    public void addTeam(String teamKey, Component displayName, Material material, int startingScore) {
        if (this.hasTeam(teamKey)) throw new IllegalArgumentException("Team already exists: '" + teamKey + "'");
        Team team = new Team(teamKey, displayName, material);
        this.teams.put(teamKey, team);
        this.scoreboardHandler.addEntry(team.getDisplayName(), startingScore);
    }

    public void removeTeamIfExists(String teamKey) {
        if (!this.hasTeam(teamKey)) { warn("Team does not exist: '" + teamKey + "'"); return; }
        this.teams.remove(teamKey);
        this.scoreboardHandler.removeEntryIfExists(teamKey);
    }

    public void removeEmptyTeams() {
        for (String teamKey : new ArrayList<>(this.getTeamNames())) {
            Team team = this.getTeam(teamKey);
            if (team.getNumPlayers() == 0) this.removeTeamIfExists(teamKey);
        }
    }

    public Team getTeam(Player player) {
        if (!this.getAllPlayers().contains(player)) throw new IllegalArgumentException("Player not part of a team: '" + player.getName() + "'");
        for (Team team : this.getTeams()) if (team.hasPlayer(player)) return team;
        return null;
    }

    public Set<Team> getWinningTeams() {
        int maxScore = this.getMaxScore();
        Set<Team> winningTeams = new HashSet<>();
        for (Team team : this.getTeams()) {
            if (this.getScore(team) == maxScore) winningTeams.add(team);
        }
        return winningTeams;
    }

    public Set<Team> getTeamsWithPositiveLives() {
        Set<Team> positiveTeams = new HashSet<>();
        for (Team team : this.getTeams()) if (this.getScore(team) > 0) positiveTeams.add(team);
        return positiveTeams;
    }

    public Set<Team> getLosingTeams() {
        int maxScore = this.getMaxScore();
        Set<Team> losingTeams = new HashSet<>();
        for (Team team : this.getTeams()) {
            if (this.getScore(team) < maxScore) losingTeams.add(team);
        }
        return losingTeams;
    }

    //-------------------------------------------------------------------------
    // Player methods
    //-------------------------------------------------------------------------
    public void addPlayer(Player player, String teamKey) {
        if (!this.hasTeam(teamKey)) throw new IllegalArgumentException("Team does not exist: '" + teamKey + "'");
        Team team = this.teams.get(teamKey);
        team.addPlayer(player);
    }

    public void addPlayer(Player player, int teamIndex) {
        if (teamIndex + 1 > this.getNumTeams()) throw new IllegalArgumentException("Attempted to add player to out-of-bounds team index: '" + teamIndex + "'");
        Team team = new ArrayList<>(this.getTeams()).get(teamIndex);
        team.addPlayer(player);
    }

    public void removePlayerIfExists(Player player) {
        if (!this.hasTeam(player)) return;
        Team team = this.getTeam(player);
        team.removePlayer(player);
    }

    public Set<Player> getPlayers(String teamKey) {
        if (!this.hasTeam(teamKey)) throw new IllegalArgumentException("Team does not exist: '" + teamKey + "'");
        return this.teams.get(teamKey).getOnlinePlayers();
    }

    public Set<OfflinePlayer> getAllPlayers() {
        Set<OfflinePlayer> players = new HashSet<>();
        for (Team team : this.getTeams()) players.addAll(team.getPlayers());
        return players;
    }

    public Set<Player> getAllOnlinePlayers() {
        Set<Player> players = new HashSet<>();
        for (Team team : this.getTeams()) players.addAll(team.getOnlinePlayers());
        return players;
    }

    public Set<UUID> getAllOnlineUUIDs() {
        Set<UUID> uuids = new HashSet<>();
        for (Team team : this.getTeams()) uuids.addAll(team.getOnlineUUIDs());
        return uuids;
    }

    public Set<Player> getLosingPlayers() {
        Set<Player> players = new HashSet<>();
        for (Team team : this.getLosingTeams()) players.addAll(team.getOnlinePlayers());
        return players;
    }

    //-------------------------------------------------------------------------
    // Score methods
    //-------------------------------------------------------------------------
    public void addScore(Team team, int delta) { this.setScore(team, this.getScore(team) + delta); }
    public void subtractScore(Team team, int delta) { this.addScore(team, -delta); }
    public void setScore(Team team, int score) {
        if (!this.hasTeam(team.getKey())) throw new IllegalArgumentException("Team does not exist: '" + team.getKey() + "'");
        this.scoreboardHandler.setScore(team.getKey(), score);
    }

    public int getScore(Team team) { return this.getScore(team.getKey()); }
    public int getScore(String teamKey) {
        if (!this.hasTeam(teamKey)) throw new IllegalArgumentException("Team does not exist: '" + teamKey + "'");
        return this.scoreboardHandler.getScore(teamKey);
    }

    public int getMaxScore() {
        if (this.getNumTeams() <= 0) throw new IllegalArgumentException("No teams exist");
        int maxScore = Integer.MIN_VALUE;
        for (Team team : this.getTeams()) if (this.getScore(team) > maxScore) maxScore = this.getScore(team);
        return maxScore;
    }

    public boolean noTeamHasPositiveLives() { return getNumPositiveLivesTeams() == 0; }
    public boolean atMostOneTeamHasPositiveLives() { return getNumPositiveLivesTeams() <= 1; }
    public boolean atLeastOneTeamHasPositiveLives() { return getNumPositiveLivesTeams() >= 1; }
    private int getNumPositiveLivesTeams() {
        int positiveTeams = 0;
        for (Team team : this.getTeams()) if (this.getScore(team) > 0) positiveTeams += 1;
        return positiveTeams;
    }

    //-------------------------------------------------------------------------
    // TeamHandler methods
    //-------------------------------------------------------------------------
    public void showScoreboardToAllPlayers() {
        for (Team team : teams.values()) {
            for (Player player : team.getOnlinePlayers()) {
                this.scoreboardHandler.showToPlayer(player);
            }
        }
    }

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
            if (!hasTeam(event.getPlayer())) return;
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