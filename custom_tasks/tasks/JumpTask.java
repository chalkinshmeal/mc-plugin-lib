package chalkinshmeal.mc_plugin_lib.custom_tasks.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;

import chalkinshmeal.mc_plugin_lib.custom_tasks.CustomTask;
import chalkinshmeal.mc_plugin_lib.teams.Team;

public class JumpTask extends CustomTask {
    private static final String configKey = "jumpTask";
    private static final String normalKey = "jumps";
    private final int targetJumps;
    private Map<Team, Integer> jumpCounts;

    //---------------------------------------------------------------------------------------------
    // Constructor, which takes lockintaskhandler
    //---------------------------------------------------------------------------------------------
    public JumpTask(int targetJumps) {
        super();
        this.targetJumps = targetJumps;
        this.description = "Jump " + this.targetJumps + " times";
        this.displayItem = new ItemStack(Material.RABBIT_FOOT);
        this.jumpCounts = new HashMap<>();
    }

    //---------------------------------------------------------------------------------------------
    // Abstract methods
    //---------------------------------------------------------------------------------------------
    public void validateConfig() {
    }

    public void addListeners() {
		this.listeners.add(new JumpTaskPlayerJumpEventListener(this));
    }

    //---------------------------------------------------------------------------------------------
    // Task getter
    //---------------------------------------------------------------------------------------------
    public static List<JumpTask> getTasks(int tier) {
        List<JumpTask> tasks = new ArrayList<>();
        int targetJumps = config.getInt(configKey + "." + normalKey + "." + tier, -1);
        if (targetJumps == -1) return tasks;

        tasks.add(new JumpTask(targetJumps));
        return tasks;
    }

    //---------------------------------------------------------------------------------------------
    // Any listeners. Upon completion, lockinTaskHandler.CompleteTask(player);
    //---------------------------------------------------------------------------------------------
    public void onPlayerJumpEvent(PlayerJumpEvent event) {
        Player player = event.getPlayer();
        Team team = CustomTask.teamHandler.getTeam(player);

        this.jumpCounts.put(team, this.jumpCounts.getOrDefault(team, 0) + 1);
        if (this.jumpCounts.get(team) < this.targetJumps) return;

        this.complete(player);
    }
}

//---------------------------------------------------------------------------------------------
// Private classes - any listeners that this task requires
//---------------------------------------------------------------------------------------------
class JumpTaskPlayerJumpEventListener implements Listener {
    private final JumpTask task;

    public JumpTaskPlayerJumpEventListener(JumpTask task) {
        this.task = task;
    }

    /** Event Handler */
    @EventHandler
    public void onPlayerJumpEvent(PlayerJumpEvent event) {
        if (this.task.haveAllTeamsCompleted()) return;
        this.task.onPlayerJumpEvent(event);
    }
}