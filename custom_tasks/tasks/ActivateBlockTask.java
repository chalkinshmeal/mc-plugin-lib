package chalkinshmeal.mc_plugin_lib.custom_tasks.tasks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.inventory.ItemStack;

import chalkinshmeal.lockin.utils.Utils;
import chalkinshmeal.mc_plugin_lib.custom_tasks.CustomTask;

public class ActivateBlockTask extends CustomTask {
    private static final String configKey = "activateBlockTask";
    private static final String normalKey = "materials";
    private final Material material;

    public ActivateBlockTask(Material material) {
        super();
        this.material = material;
        this.description = "Activate a " + Utils.getReadableMaterialName(material);
        this.displayItem = new ItemStack(this.material);
        maxTaskCount = config.getInt(configKey + "." + "maxTaskCount", 1);
    }

    //---------------------------------------------------------------------------------------------
    // Abstract methods
    //---------------------------------------------------------------------------------------------
    public void addListeners() {
		this.listeners.add(new ActivateBlockTaskPlayerCraftListener(this));
    }

    public static List<ActivateBlockTask> getTasks(int tier) {
        List<ActivateBlockTask> tasks = new ArrayList<>();
        List<String> materialStrs = Utils.getRandomItems(config.getListFromKey(configKey + "." + normalKey + "." + tier), maxTaskCount);
        if (materialStrs.size() == 0) return tasks;

        int loopCount = Math.min(maxTaskCount, materialStrs.size());
        for (int i = 0; i < loopCount; i++) {
            Material material = Material.valueOf(materialStrs.get(i));
            tasks.add(new ActivateBlockTask(material));
        }
        return tasks;
    }

    //---------------------------------------------------------------------------------------------
    // Listeners
    //---------------------------------------------------------------------------------------------
    public void onBlockRedstoneEvent(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        if (event.getOldCurrent() >= event.getNewCurrent()) return;

        for (Block adjacentBlock : Utils.getAdjacentBlocks(block)) {
            if (adjacentBlock.getType() == this.material) {
                Player player = Utils.getClosestPlayer(adjacentBlock.getLocation(), 5);
                this.complete(player);
            }
        }
    }
}

//---------------------------------------------------------------------------------------------
// Private classes - any listeners that this task requires
//---------------------------------------------------------------------------------------------
class ActivateBlockTaskPlayerCraftListener implements Listener {
    private final ActivateBlockTask task;

    public ActivateBlockTaskPlayerCraftListener(ActivateBlockTask task) {
        this.task = task;
    }

    /** Event Handler */
    @EventHandler
    public void onBlockRedstoneEvent(BlockRedstoneEvent event) {
        if (this.task.haveAllTeamsCompleted()) return;
        this.task.onBlockRedstoneEvent(event);
    }
}