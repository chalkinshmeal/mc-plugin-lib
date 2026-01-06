package chalkinshmeal.mc_plugin_lib.custom_tasks.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import chalkinshmeal.mc_plugin_lib.custom_tasks.CustomTask;
import chalkinshmeal.lockin.utils.Utils;
import chalkinshmeal.mc_plugin_lib.teams.Team;



public class PlaceItemsTask extends CustomTask {
    private static final String configKey = "placeItemsTask";
    private static final String normalKey = "materials";
    private final Material material;
    private final int amount;
    private final Map<Team, Integer> placedItems;

    //---------------------------------------------------------------------------------------------
    // Constructor, which takes lockintaskhandler
    //---------------------------------------------------------------------------------------------
    public PlaceItemsTask(Material material, int amount) {
        super();
        this.material = material;
        this.amount = amount;
        this.placedItems = new HashMap<>();
        this.description = "Place " + this.amount + " " + Utils.getReadableMaterialName(material);
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
		this.listeners.add(new PlaceItemsTaskBlockPlaceEventListener(this));
    }

    //---------------------------------------------------------------------------------------------
    // Task getter
    //---------------------------------------------------------------------------------------------
    public static List<PlaceItemsTask> getTasks(int tier) {
        List<PlaceItemsTask> tasks = new ArrayList<>();
        int taskCount = config.getInt(configKey + "." + maxTaskCount, 1);
        String subKey = normalKey;
        List<String> materialStrs = Utils.getRandomItems(config.getKeyListFromKey(configKey + "." + subKey + "." + tier), taskCount);
        int loopCount = Math.min(taskCount, materialStrs.size());

        for (int i = 0; i < loopCount; i++) {
            String materialStr = materialStrs.get(i);
            Material material = Material.valueOf(materialStrs.get(i));
            int amount = config.getInt(configKey + "." + subKey + "." + tier + "." + materialStr, 1);
            tasks.add(new PlaceItemsTask(material, amount));
        }
        return tasks;
    }

    //---------------------------------------------------------------------------------------------
    // Any listeners. Upon completion, lockinTaskHandler.CompleteTask(player);
    //---------------------------------------------------------------------------------------------
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        // Return if material does not match
        Material itemType = event.getBlock().getType();
        if (itemType != this.material) return;

        // Return if 
        Player player = event.getPlayer();
        Team team = CustomTask.teamHandler.getTeam(player);
        this.placedItems.put(team, this.placedItems.getOrDefault(team, 0) + 1);
        if (this.placedItems.get(team) < this.amount) return;
        this.complete(player);
    }
}

//---------------------------------------------------------------------------------------------
// Private classes - any listeners that this task requires
//---------------------------------------------------------------------------------------------
class PlaceItemsTaskBlockPlaceEventListener implements Listener {
    private final PlaceItemsTask task;

    public PlaceItemsTaskBlockPlaceEventListener(PlaceItemsTask task) {
        this.task = task;
    }

    /** Event Handler */
    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (this.task.haveAllTeamsCompleted()) return;
        this.task.onBlockPlaceEvent(event);
    }
}
