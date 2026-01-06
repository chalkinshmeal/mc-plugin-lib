package chalkinshmeal.mc_plugin_lib.custom_tasks.tasks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.inventory.ItemStack;

import chalkinshmeal.mc_plugin_lib.custom_tasks.CustomTask;
import chalkinshmeal.lockin.utils.Utils;

public class SleepInColoredBedTask extends CustomTask {
    private static final String configKey = "sleepInColoredBedTask";
    private static final String normalKey = "dyeColors";
    private final DyeColor dyeColor;

    //---------------------------------------------------------------------------------------------
    // Constructor, which takes lockintaskhandler
    //---------------------------------------------------------------------------------------------
    public SleepInColoredBedTask(DyeColor dyeColor) {
        super();
        this.dyeColor = dyeColor;
        this.description = "Sleep in a " + Utils.getReadableDyeColorName(this.dyeColor) + " colored bed";
        this.displayItem = new ItemStack(Utils.getBedMaterial(this.dyeColor));
        
    }

    //---------------------------------------------------------------------------------------------
    // Abstract methods
    //---------------------------------------------------------------------------------------------
    public void validateConfig() {
        for (String tierStr : config.getKeyListFromKey(configKey + "." + normalKey)) {
            for (String valueStr : config.getListFromKey(configKey + "." + normalKey + "." + tierStr)) {
                DyeColor.valueOf(valueStr);
            }
        }
    }

    public void addListeners() {
		this.listeners.add(new SleepInColoredBedTaskListener(this));
    }

    //---------------------------------------------------------------------------------------------
    // Task getter
    //---------------------------------------------------------------------------------------------
    public static List<SleepInColoredBedTask> getTasks(int tier) {
        List<SleepInColoredBedTask> tasks = new ArrayList<>();
        int taskCount = config.getInt(configKey + "." + maxTaskCount, 1);
        List<String> dyeColorStrs = Utils.getRandomItems(config.getListFromKey(configKey + "." + normalKey + "." + tier), taskCount);
        int loopCount = Math.min(taskCount, dyeColorStrs.size());

        for (int i = 0; i < loopCount; i++) {
            DyeColor dyeColor = DyeColor.valueOf(dyeColorStrs.get(i));
            tasks.add(new SleepInColoredBedTask(dyeColor));
        }
        return tasks;
    }

    //---------------------------------------------------------------------------------------------
    // Any listeners. Upon completion, lockinTaskHandler.CompleteTask(player);
    //---------------------------------------------------------------------------------------------
    public void onPlayerBedEnterEvent(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        Block bedBlock = event.getBed();

        // Check if the bed is made of the target color
        if (this.dyeColor != Utils.getDyeColorFromMaterial(bedBlock.getType())) return;

        this.complete(player);
    }
}

//---------------------------------------------------------------------------------------------
// Private classes - any listeners that this task requires
//---------------------------------------------------------------------------------------------
class SleepInColoredBedTaskListener implements Listener {
    private final SleepInColoredBedTask task;

    public SleepInColoredBedTaskListener(SleepInColoredBedTask task) {
        this.task = task;
    }

    /** Event Handler */
    @EventHandler
    public void onPlayerBedEnterEvent(PlayerBedEnterEvent event) {
        if (this.task.haveAllTeamsCompleted()) return;
        this.task.onPlayerBedEnterEvent(event);
    }
}

