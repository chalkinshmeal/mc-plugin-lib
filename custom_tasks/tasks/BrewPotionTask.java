package chalkinshmeal.mc_plugin_lib.custom_tasks.tasks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.ItemStack;

import chalkinshmeal.mc_plugin_lib.custom_tasks.CustomTask;
import chalkinshmeal.lockin.utils.Utils;

public class BrewPotionTask extends CustomTask {
    //---------------------------------------------------------------------------------------------
    // Constructor, which takes lockintaskhandler
    //---------------------------------------------------------------------------------------------
    public BrewPotionTask() {
        super();
        this.description = "Brew a potion";
        this.displayItem = new ItemStack(Material.BREWING_STAND);
    }

    //---------------------------------------------------------------------------------------------
    // Abstract methods
    //---------------------------------------------------------------------------------------------
    public void validateConfig() {}

    public void addListeners() {
		this.listeners.add(new BrewPotionTaskBrewEventListener(this));
    }

    //---------------------------------------------------------------------------------------------
    // Task getter
    //---------------------------------------------------------------------------------------------
    public static List<BrewPotionTask> getTasks(int tier) {
        if (tier != 8) return new ArrayList<>();
        List<BrewPotionTask> tasks = new ArrayList<>();
        tasks.add(new BrewPotionTask());
        return tasks;
    }

    //---------------------------------------------------------------------------------------------
    // Any listeners. Upon completion, lockinTaskHandler.CompleteTask(player);
    //---------------------------------------------------------------------------------------------
    public void onBrewEvent(BrewEvent event) {
        Player player = Utils.getClosestPlayer(event.getBlock().getLocation(), 5);

        this.complete(player);
    }
}

//---------------------------------------------------------------------------------------------
// Private classes - any listeners that this task requires
//---------------------------------------------------------------------------------------------
class BrewPotionTaskBrewEventListener implements Listener {
    private final BrewPotionTask task;

    public BrewPotionTaskBrewEventListener(BrewPotionTask task) {
        this.task = task;
    }

    /** Event Handler */
    @EventHandler
    public void onBrewEvent(BrewEvent event) {
        if (this.task.haveAllTeamsCompleted()) return;
        this.task.onBrewEvent(event);
    }
}
