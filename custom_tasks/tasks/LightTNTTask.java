package chalkinshmeal.mc_plugin_lib.custom_tasks.tasks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import chalkinshmeal.mc_plugin_lib.custom_tasks.CustomTask;

public class LightTNTTask extends CustomTask {
    //---------------------------------------------------------------------------------------------
    // Constructor, which takes lockintaskhandler
    //---------------------------------------------------------------------------------------------
    public LightTNTTask() {
        super();
        this.description = "Light a TNT with flint and steel";
        this.displayItem = new ItemStack(Material.TNT);
        
    }

    //---------------------------------------------------------------------------------------------
    // Abstract methods
    //---------------------------------------------------------------------------------------------
    public void validateConfig() {}

    public void addListeners() {
		this.listeners.add(new LightTNTTaskListener(this));
    }

    public static List<LightTNTTask> getTasks(int tier) {
        if (tier != 2) return new ArrayList<>();
        List<LightTNTTask> tasks = new ArrayList<>();
        tasks.add(new LightTNTTask());
        return tasks;
    }

    //---------------------------------------------------------------------------------------------
    // Any listeners. Upon completion, lockinTaskHandler.CompleteTask(player);
    //---------------------------------------------------------------------------------------------
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (block.getType() != Material.TNT) return;
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().getType() != Material.FLINT_AND_STEEL) return;

        this.complete(player);
    }
}

//---------------------------------------------------------------------------------------------
// Private classes - any listeners that this task requires
//---------------------------------------------------------------------------------------------
class LightTNTTaskListener implements Listener {
    private final LightTNTTask task;

    public LightTNTTaskListener(LightTNTTask task) {
        this.task = task;
    }

    /** Event Handler */
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (this.task.haveAllTeamsCompleted()) return;
        this.task.onPlayerInteractEvent(event);
    }
}

