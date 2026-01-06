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

public class EquipArmorTask extends CustomTask {
    //---------------------------------------------------------------------------------------------
    // Constructor, which takes lockintaskhandler
    //---------------------------------------------------------------------------------------------
    public EquipArmorTask() {
        super();
        this.description = "Equip armor";
        this.displayItem = new ItemStack(Material.CHAINMAIL_BOOTS);
    }

    //---------------------------------------------------------------------------------------------
    // Abstract methods
    //---------------------------------------------------------------------------------------------
    public void validateConfig() {}

    public void addListeners() {
		this.listeners.add(new EquipArmorTaskPlayerArmorChangeListener(this));
    }

    //---------------------------------------------------------------------------------------------
    // Task getter
    //---------------------------------------------------------------------------------------------
    public static List<EquipArmorTask> getTasks(int tier) {
        List<EquipArmorTask> tasks = new ArrayList<>();
        if (tier != 2) return tasks;

        tasks.add(new EquipArmorTask());
        return tasks;
    }

    //---------------------------------------------------------------------------------------------
    // Any listeners. Upon completion, lockinTaskHandler.CompleteTask(player);
    //---------------------------------------------------------------------------------------------
    public void onPlayerArmorChangeEvent(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = event.getNewItem();
        if (newItem == null) return;

        this.complete(player);
    }
}

//---------------------------------------------------------------------------------------------
// Private classes - any listeners that this task requires
//---------------------------------------------------------------------------------------------
class EquipArmorTaskPlayerArmorChangeListener implements Listener {
    private final EquipArmorTask task;

    public EquipArmorTaskPlayerArmorChangeListener(EquipArmorTask task) {
        this.task = task;
    }

    /** Event Handler */
    @EventHandler
    public void onPlayerArmorChangeEvent(PlayerArmorChangeEvent event) {
        if (this.task.haveAllTeamsCompleted()) return;
        this.task.onPlayerArmorChangeEvent(event);
    }
}

