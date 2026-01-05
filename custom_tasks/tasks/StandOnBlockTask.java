package chalkinshmeal.mc_plugin_lib.custom_tasks.tasks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import chalkinshmeal.mc_plugin_lib.custom_tasks.CustomTask;
import chalkinshmeal.lockin.utils.Utils;

public class StandOnBlockTask extends CustomTask {
    private static final String configKey = "standOnBlockTask";
    private static final String normalKey = "materials";
    private final Material material;

    //---------------------------------------------------------------------------------------------
    // Constructor, which takes lockintaskhandler
    //---------------------------------------------------------------------------------------------
    public StandOnBlockTask(Material material) {
        super();
        this.material = material;
        this.description = "Stand on " + Utils.getReadableMaterialName(material);
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
		this.listeners.add(new StandOnBlockTaskPlayerCraftListener(this));
    }

    //---------------------------------------------------------------------------------------------
    // Task getter
    //---------------------------------------------------------------------------------------------
    public static List<StandOnBlockTask> getTasks(int tier) {
        List<StandOnBlockTask> tasks = new ArrayList<>();
        int taskCount = config.getInt(configKey + "." + maxTaskCount, 1);
        String subKey = normalKey;
        List<String> materialStrs = Utils.getRandomItems(config.getListFromKey(configKey + "." + subKey + "." + tier), taskCount);
        int loopCount = Math.min(taskCount, materialStrs.size());

        for (int i = 0; i < loopCount; i++) {
            Material material = Material.valueOf(materialStrs.get(i));
            tasks.add(new StandOnBlockTask(material));
        }
        return tasks;
    }

    //---------------------------------------------------------------------------------------------
    // Any listeners. Upon completion, lockinTaskHandler.CompleteTask(player);
    //---------------------------------------------------------------------------------------------
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Block blockBelow = player.getLocation().subtract(0, 1, 0).getBlock();

        if (blockBelow.getType() != this.material) return;

        this.complete(player);
    }
}

//---------------------------------------------------------------------------------------------
// Private classes - any listeners that this task requires
//---------------------------------------------------------------------------------------------
class StandOnBlockTaskPlayerCraftListener implements Listener {
    private final StandOnBlockTask task;

    public StandOnBlockTaskPlayerCraftListener(StandOnBlockTask task) {
        this.task = task;
    }

    /** Event Handler */
    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        if (this.task.haveAllTeamsCompleted()) return;
        this.task.onPlayerMoveEvent(event);
    }
}

