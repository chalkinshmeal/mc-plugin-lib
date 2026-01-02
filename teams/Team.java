package chalkinshmeal.mc_plugin_lib.teams;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;

//-----------------------------------------------------------------------------
// Team class
// A class to hold a team of players
// NOT the same as Spigot API's Scoreboard.Team
//-----------------------------------------------------------------------------
public class Team {
    private final String key;
    private Component displayName;
    private Material material;
    private final Set<UUID> playerUUIDs = new HashSet<>();

    public Team(String key, Component displayName) {
        this.key = key;
        this.displayName = displayName;
    }

    public Team(String key, Component displayName, Material material) {
        this.key = key;
        this.displayName = displayName;
        this.material = material;
    }

    //-------------------------------------------------------------------------
    // Accessor/Mutators
    //-------------------------------------------------------------------------
    public String getKey() { return this.key; }
    public Component getDisplayName() { return this.displayName; }
    public Material getMaterial() { return this.material; }

    //-------------------------------------------------------------------------
    // Player methods
    //-------------------------------------------------------------------------
    public void addPlayer(Player player) { playerUUIDs.add(player.getUniqueId()); }
    public void removePlayer(Player player) { playerUUIDs.remove(player.getUniqueId()); }
    public boolean hasPlayer(Player player) { return playerUUIDs.contains(player.getUniqueId()); }
    public Set<UUID> getUUIDs() { return this.playerUUIDs; }
    public Set<UUID> getOnlineUUIDs() {
        Set<UUID> onlineUUIDs = new HashSet<>();
        for (UUID uuid : this.getUUIDs()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if (offlinePlayer.isOnline()) onlineUUIDs.add(uuid);
        }
        return onlineUUIDs;
    }

    public Set<OfflinePlayer> getPlayers() {
        Set<OfflinePlayer> players = new HashSet<>();
        for (UUID uuid : this.getUUIDs()) players.add(Bukkit.getOfflinePlayer(uuid));
        return players;
    }
    public Set<Player> getOnlinePlayers() {
        Set<Player> players = new HashSet<>();
        for (OfflinePlayer offlinePlayer : this.getPlayers()) {
            if (offlinePlayer.isOnline()) players.add(offlinePlayer.getPlayer());
        }
        return players;
    }
    public int getNumPlayers() { return this.playerUUIDs.size(); }
    public Set<String> getPlayerNames() {
        Set<String> playerNames = new HashSet<>();
        for (UUID uuid : this.getUUIDs()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            String playerName = offlinePlayer.getName();
            playerNames.add(playerName);
            //playerNames.add(Bukkit.getPlayer(uuid).getName());
        }
        return playerNames;
    }

    //-------------------------------------------------------------------------
    // Debug
    //-------------------------------------------------------------------------
    public String toString() {
        return "[Team] Key: " + this.getKey() + ", Player Count: " + this.getNumPlayers() + ")";
    }
}