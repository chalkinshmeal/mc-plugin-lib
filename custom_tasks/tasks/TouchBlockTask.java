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

public class TouchBlockTask extends CustomTask {
    private static final String configKey = "touchBlockTask";
    private static final String normalKey = "materials";
    private final Material material;

    //---------------------------------------------------------------------------------------------
    // Constructor, which takes lockintaskhandler
    //---------------------------------------------------------------------------------------------
    public TouchBlockTask(Material material) {
        super();
        this.material = material;
        this.description = "Touch " + Utils.getReadableMaterialName(material) + " (Standing on or be within)";
        try {
            this.displayItem = new ItemStack(this.material);
        }
        catch (IllegalArgumentException e) {
            this.displayItem = new ItemStack(Material.BRUSH);
        }
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
		this.listeners.add(new TouchBlockTaskPlayerCraftListener(this));
    }

    //---------------------------------------------------------------------------------------------
    // Task getter
    //---------------------------------------------------------------------------------------------
    public static List<TouchBlockTask> getTasks(int tier) {
        List<TouchBlockTask> tasks = new ArrayList<>();
        int taskCount = config.getInt(configKey + "." + maxTaskCount, 1);
        String subKey = normalKey;
        List<String> materialStrs = Utils.getRandomItems(config.getListFromKey(configKey + "." + subKey + "." + tier), taskCount);
        int loopCount = Math.min(taskCount, materialStrs.size());

        for (int i = 0; i < loopCount; i++) {
            Material material = Material.valueOf(materialStrs.get(i));
            tasks.add(new TouchBlockTask(material));
        }
        return tasks;
    }

    //---------------------------------------------------------------------------------------------
    // Any listeners. Upon completion, lockinTaskHandler.CompleteTask(player);
    //---------------------------------------------------------------------------------------------
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Block blockBelow = player.getLocation().subtract(0, 1, 0).getBlock();
        Block blockLower = player.getLocation().getBlock();
        Block blockUpper = player.getLocation().add(0, 1, 0).getBlock();

        if (blockBelow.getType() == this.material || blockLower.getType() == this.material || blockUpper.getType() == this.material) {
            this.complete(player);
        }
    }
}

//---------------------------------------------------------------------------------------------
// Private classes - any listeners that this task requires
//---------------------------------------------------------------------------------------------
class TouchBlockTaskPlayerCraftListener implements Listener {
    private final TouchBlockTask task;

    public TouchBlockTaskPlayerCraftListener(TouchBlockTask task) {
        this.task = task;
    }

    /** Event Handler */
    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        if (this.task.haveAllTeamsCompleted()) return;
        this.task.onPlayerMoveEvent(event);
    }
}

