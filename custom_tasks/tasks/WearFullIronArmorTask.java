package chalkinshmeal.mc_plugin_lib.custom_tasks.tasks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;

import chalkinshmeal.mc_plugin_lib.custom_tasks.CustomTask;

public class WearFullIronArmorTask extends CustomTask {
    //---------------------------------------------------------------------------------------------
    // Constructor, which takes lockintaskhandler
    //---------------------------------------------------------------------------------------------
    public WearFullIronArmorTask() {
        super();
        this.description = "Wear a full set of iron armor";
        this.displayItem = new ItemStack(Material.IRON_CHESTPLATE);
        
    }

    //---------------------------------------------------------------------------------------------
    // Abstract methods
    //---------------------------------------------------------------------------------------------
    public void validateConfig() {}

    public void addListeners() {
		this.listeners.add(new WearFullIronArmorTaskPlayerArmorChangeListener(this));
    }

    public static List<WearFullIronArmorTask> getTasks(int tier) {
        if (tier != 5) return new ArrayList<>();
        List<WearFullIronArmorTask> tasks = new ArrayList<>();
        tasks.add(new WearFullIronArmorTask());
        return tasks;
    }

    //---------------------------------------------------------------------------------------------
    // Any listeners. Upon completion, lockinTaskHandler.CompleteTask(player);
    //---------------------------------------------------------------------------------------------
    public void onPlayerArmorChangeEvent(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();

        if (player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getType() == Material.IRON_HELMET
        && player.getInventory().getChestplate() != null && player.getInventory().getChestplate().getType() == Material.IRON_CHESTPLATE
        && player.getInventory().getLeggings() != null && player.getInventory().getLeggings().getType() == Material.IRON_LEGGINGS
        && player.getInventory().getBoots() != null && player.getInventory().getBoots().getType() == Material.IRON_BOOTS) {
            this.complete(event.getPlayer());
        }
    }
}

//---------------------------------------------------------------------------------------------
// Private classes - any listeners that this task requires
//---------------------------------------------------------------------------------------------
class WearFullIronArmorTaskPlayerArmorChangeListener implements Listener {
    private final WearFullIronArmorTask task;

    public WearFullIronArmorTaskPlayerArmorChangeListener(WearFullIronArmorTask task) {
        this.task = task;
    }

    /** Event Handler */
    @EventHandler
    public void onPlayerArmorChangeEvent(PlayerArmorChangeEvent event) {
        if (this.task.haveAllTeamsCompleted()) return;
        this.task.onPlayerArmorChangeEvent(event);
    }
}

