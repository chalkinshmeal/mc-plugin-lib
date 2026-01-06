package chalkinshmeal.mc_plugin_lib.custom_tasks.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import chalkinshmeal.mc_plugin_lib.custom_tasks.CustomTask;
import chalkinshmeal.mc_plugin_lib.teams.Team;

public class DieTask extends CustomTask {
    private static final String configKey = "deathTask";
    private static final String normalKey = "deaths";
    private final int targetDeaths;
    private Map<Team, Integer> deathCounts;

    //---------------------------------------------------------------------------------------------
    // Constructor, which takes lockintaskhandler
    //---------------------------------------------------------------------------------------------
    public DieTask(int targetDeaths) {
        super();
        this.targetDeaths = targetDeaths;
        this.description = "Die " + this.targetDeaths + " times";
        this.displayItem = new ItemStack(Material.TOTEM_OF_UNDYING);
        this.deathCounts = new HashMap<>();
        this.applyAAnRules = false;
    }

    //---------------------------------------------------------------------------------------------
    // Abstract methods
    //---------------------------------------------------------------------------------------------
    public void validateConfig() {}

    public void addListeners() {
		this.listeners.add(new DieTaskPlayerDeathEventListener(this));
    }

    //---------------------------------------------------------------------------------------------
    // Task getter
    //---------------------------------------------------------------------------------------------
    public static List<DieTask> getTasks(int tier) {
        List<DieTask> tasks = new ArrayList<>();
        int targetDeaths = config.getInt(configKey + "." + normalKey + "." + tier, -1);
        if (targetDeaths == -1) return tasks;

        tasks.add(new DieTask(targetDeaths));
        return tasks;
    }

    //---------------------------------------------------------------------------------------------
    // Any listeners. Upon completion, lockinTaskHandler.CompleteTask(player);
    //---------------------------------------------------------------------------------------------
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        Team team = CustomTask.teamHandler.getTeam(player);

        this.deathCounts.put(team, this.deathCounts.getOrDefault(team, 0) + 1);
        if (this.deathCounts.get(team) < this.targetDeaths) return;

        this.complete(player);
    }
}

//---------------------------------------------------------------------------------------------
// Private classes - any listeners that this task requires
//---------------------------------------------------------------------------------------------
class DieTaskPlayerDeathEventListener implements Listener {
    private final DieTask task;

    public DieTaskPlayerDeathEventListener(DieTask task) {
        this.task = task;
    }

    /** Event Handler */
    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        if (this.task.haveAllTeamsCompleted()) return;
        this.task.onPlayerDeathEvent(event);
    }
}