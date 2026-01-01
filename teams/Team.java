package chalkinshmeal.mc_plugin_lib.teams;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;

//-----------------------------------------------------------------------------
// Team class
// A class to hold a team of players
// NOT the same as Spigot API's Scoreboard.Team
//-----------------------------------------------------------------------------
public class Team {
    private final String name;
    private Component displayName;
    private final Set<UUID> players = new HashSet<>();

    public Team(String name, Component displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    //-------------------------------------------------------------------------
    // Accessor/Mutators
    //-------------------------------------------------------------------------
    public String getName() { return this.name; }
    public Component getDisplayName() { return this.displayName; }

    //-------------------------------------------------------------------------
    // Player methods
    //-------------------------------------------------------------------------
    public void addPlayer(Player player) { players.add(player.getUniqueId()); }
    public void removePlayer(Player player) { players.remove(player.getUniqueId()); }
    public boolean hasPlayer(Player player) { return players.contains(player.getUniqueId()); }
    public Set<UUID> getUUIDs() { return Collections.unmodifiableSet(players); }
    public Set<Player> getOnlinePlayers() {
        Set<Player> players = new HashSet<>();
        for (UUID uuid : this.getUUIDs()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if (offlinePlayer.isOnline()) players.add(offlinePlayer.getPlayer());
        }
        return players;
    }
}