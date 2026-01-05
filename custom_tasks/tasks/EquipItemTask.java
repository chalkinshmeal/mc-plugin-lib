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
import chalkinshmeal.lockin.utils.Utils;

public class EquipItemTask extends CustomTask {
    private static final String configKey = "equipItemTask";
    private static final String normalKey = "materials";
    private final Material material;

    //---------------------------------------------------------------------------------------------
    // Constructor, which takes lockintaskhandler
    //---------------------------------------------------------------------------------------------
    public EquipItemTask(Material material) {
        super();
        this.material = material;
        this.description = "Equip a " + Utils.getReadableMaterialName(material);
        this.displayItem = new ItemStack(this.material);
    }

    //---------------------------------------------------------------------------------------------
    // Abstract methods
    //---------------------------------------------------------------------------------------------
    public void validateConfig() {
        for (String tierStr : config.getKeyListFromKey(configKey + "." + normalKey)) {
            for (String valueStr : config.getListFromKey(configKey + "." + normalKey + "." + tierStr)) {
                Material.valueOf(valueStr);
            }
        }
    }

    public void addListeners() {
		this.listeners.add(new EquipItemTaskPlayerCraftListener(this));
    }

    //---------------------------------------------------------------------------------------------
    // Task getter
    //---------------------------------------------------------------------------------------------
    public static List<EquipItemTask> getTasks(int tier) {
        List<EquipItemTask> tasks = new ArrayList<>();
        int taskCount = config.getInt(configKey + "." + maxTaskCount, 1);
        String subKey = normalKey;
        List<String> materialStrs = Utils.getRandomItems(config.getListFromKey(configKey + "." + subKey + "." + tier), taskCount);
        int loopCount = Math.min(taskCount, materialStrs.size());

        for (int i = 0; i < loopCount; i++) {
            Material material = Material.valueOf(materialStrs.get(i));
            tasks.add(new EquipItemTask(material));
        }
        return tasks;
    }

    //---------------------------------------------------------------------------------------------
    // Any listeners. Upon completion, lockinTaskHandler.CompleteTask(player);
    //---------------------------------------------------------------------------------------------
    public void onPlayerArmorChangeEvent(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = event.getNewItem();

        if (newItem == null) return;
        if (newItem.getType() != this.material) return;

        this.complete(player);
    }
}

//---------------------------------------------------------------------------------------------
// Private classes - any listeners that this task requires
//---------------------------------------------------------------------------------------------
class EquipItemTaskPlayerCraftListener implements Listener {
    private final EquipItemTask task;

    public EquipItemTaskPlayerCraftListener(EquipItemTask task) {
        this.task = task;
    }

    /** Event Handler */
    @EventHandler
    public void onPlayerArmorChangeEvent(PlayerArmorChangeEvent event) {
        if (this.task.haveAllTeamsCompleted()) return;
        this.task.onPlayerArmorChangeEvent(event);
    }
}

