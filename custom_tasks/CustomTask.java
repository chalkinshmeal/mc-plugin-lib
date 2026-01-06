package chalkinshmeal.mc_plugin_lib.custom_tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import chalkinshmeal.mc_plugin_lib.config.ConfigFile;
import chalkinshmeal.mc_plugin_lib.items.ItemStackUtils;
import chalkinshmeal.mc_plugin_lib.strings.StringUtils;
import chalkinshmeal.mc_plugin_lib.teams.Team;
import chalkinshmeal.mc_plugin_lib.teams.TeamHandler;
import chalkinshmeal.lockin.artifacts.tasks.CustomTaskHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public abstract class CustomTask {
    // Some members are made static to allow access to all instances of the class
    protected static JavaPlugin plugin;
    protected static ConfigFile config;
    protected static TeamHandler teamHandler;
    protected static CustomTaskHandler customTaskHandler;
    protected String description;
    protected ItemStack displayItem;
    protected NamedTextColor displayColor;
    protected List<Listener> listeners;
    protected HashMap<String, Boolean> teamStatuses;
    protected boolean applyAAnRules;
    protected static int maxTaskCount; // In a given round, maximum number of this type of task that can show up
    protected boolean running = false;

    //---------------------------------------------------------------------------------------------
    // Constructor
    //---------------------------------------------------------------------------------------------
    public CustomTask() {
        this.description = "";
        this.displayItem = null;
        this.displayColor = NamedTextColor.BLUE;
        this.listeners = new ArrayList<>();
        this.teamStatuses = new HashMap<>();
        this.applyAAnRules = true;
    }

    //---------------------------------------------------------------------------------------------
    // Abstract methods
    //---------------------------------------------------------------------------------------------
    public abstract void addListeners();
    // Need to define a public static <T> getTasks(int tier)

    //---------------------------------------------------------------------------------------------
    // Accessor/Mutators
    //---------------------------------------------------------------------------------------------
    public static void setPlugin(JavaPlugin plugin) { CustomTask.plugin = plugin; }
    public static void setConfig(ConfigFile config) { CustomTask.config = config; }
    public static void setTeamHandler(TeamHandler teamHandler) { CustomTask.teamHandler = teamHandler; }
    public static void setCustomTaskHandler(CustomTaskHandler customTaskHandler) { CustomTask.customTaskHandler = customTaskHandler; }
    public String getDescription() { return this.description; }
    public ItemStack getDisplayItem() { return this.displayItem; }
    public void setDisplayItem(ItemStack item) { this.displayItem = item; }
    public NamedTextColor getDisplayColor() { return this.displayColor; }
    public void updateStatus() {
        List<Component> loreLines = new ArrayList<>();

        loreLines.add(Component.text("Status:", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));

        for (String teamName : teamHandler.getTeamNames()) {
            boolean isComplete = this.hasCompleted(teamName);

            NamedTextColor statusColor = (isComplete) ? NamedTextColor.GREEN : NamedTextColor.RED;
            String statusText = (isComplete) ? ":)" : ":(";
            loreLines.add(
                Component.text(" " + teamName, NamedTextColor.DARK_AQUA).decoration(TextDecoration.ITALIC, false)
                .append(Component.text(" " + statusText, statusColor))
            );
        }
        this.displayItem = ItemStackUtils.setLore(this.displayItem, loreLines);
    }
    public boolean hasCompleted(String teamName) { return this.teamStatuses.getOrDefault(teamName, false); }
    public boolean haveAllTeamsCompleted() {
        for (String teamName : teamHandler.getTeamNames()) {
            if (!this.hasCompleted(teamName)) return false;
        }
        return true;
    }

    //---------------------------------------------------------------------------------------------
    // Task methods
    //---------------------------------------------------------------------------------------------
    public void start() {
        if (this.applyAAnRules) this.description = StringUtils.applyAAnRules(this.description);
        this.setDisplayItem(ItemStackUtils.setDisplayName(this.displayItem,
            StringUtils.stringToComponent(this.description).decoration(TextDecoration.ITALIC, false)));

        this.updateStatus();
        this.addListeners();
        this.running = true;
    }

    public void stop() {
        this.unRegisterListeners();
        this.running = false;
    }

    //---------------------------------------------------------------------------------------------
    // Task methods
    //---------------------------------------------------------------------------------------------
    public void complete(Player player) {
        Team team = CustomTask.teamHandler.getTeam(player);
        if (this.hasCompleted(team.getKey())) return;

        this.updateStatus();

        this.teamStatuses.put(team.getKey(), true);
        customTaskHandler.complete(this, player);
    }

    //---------------------------------------------------------------------------------------------
    // Listener methods
    //---------------------------------------------------------------------------------------------
    public void registerListeners() {
		PluginManager manager = plugin.getServer().getPluginManager();
        for (Listener l : this.listeners) { manager.registerEvents(l, plugin); }
    }
    public void unRegisterListeners() {
        for (Listener l : this.listeners) { HandlerList.unregisterAll(l); }
    }
}