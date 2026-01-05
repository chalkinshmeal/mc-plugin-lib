package chalkinshmeal.mc_plugin_lib.custom_tasks.tasks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;

import chalkinshmeal.mc_plugin_lib.custom_tasks.CustomTask;
import chalkinshmeal.lockin.utils.Utils;

public class CreateEntityTask extends CustomTask {
    private static final String configKey = "createEntityTask";
    private static final String normalKey = "entityTypes";
    private final EntityType createType;

    //---------------------------------------------------------------------------------------------
    // Constructor, which takes lockintaskhandler
    //---------------------------------------------------------------------------------------------
    public CreateEntityTask(EntityType createType) {
        super();
        this.createType = createType;
        this.description = "Create a " + Utils.getReadableEntityTypeName(this.createType);
        this.displayItem = new ItemStack(Material.CARVED_PUMPKIN);
    }

    //---------------------------------------------------------------------------------------------
    // Abstract methods
    //---------------------------------------------------------------------------------------------
    public void validateConfig() {
        for (String tierStr : config.getKeyListFromKey(configKey + "." + normalKey)) {
            for (String valueStr : config.getListFromKey(configKey + "." + normalKey + "." + tierStr)) {
                EntityType.valueOf(valueStr);
            }
        }
    }

    public void addListeners() {
		this.listeners.add(new CreateEntityTaskListener(this));
    }

    //---------------------------------------------------------------------------------------------
    // Task getter
    //---------------------------------------------------------------------------------------------
    public static List<CreateEntityTask> getTasks(int tier) {
        List<CreateEntityTask> tasks = new ArrayList<>();
        int taskCount = config.getInt(configKey + "." + maxTaskCount, 1);
        List<String> entityTypeStrs = Utils.getRandomItems(config.getListFromKey(configKey + "." + normalKey + "." + tier), taskCount);
        int loopCount = Math.min(taskCount, entityTypeStrs.size());

        for (int i = 0; i < loopCount; i++) {
            EntityType entityType = EntityType.valueOf(entityTypeStrs.get(i));
            tasks.add(new CreateEntityTask(entityType));
        }
        return tasks;
    }

    //---------------------------------------------------------------------------------------------
    // Any listeners. Upon completion, lockinTaskHandler.CompleteTask(player);
    //---------------------------------------------------------------------------------------------
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        EntityType entityType = event.getEntityType();
        if (entityType != this.createType) return;

        Player player = Utils.getClosestPlayer(event.getLocation(), 5);
        if (player == null) return;

        this.complete(player);
    }
}

//---------------------------------------------------------------------------------------------
// Private classes - any listeners that this task requires
//---------------------------------------------------------------------------------------------
class CreateEntityTaskListener implements Listener {
    private final CreateEntityTask task;

    public CreateEntityTaskListener(CreateEntityTask task) {
        this.task = task;
    }

    /** Event Handler */
    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        if (this.task.haveAllTeamsCompleted()) return;
        this.task.onCreatureSpawnEvent(event);
    }
}

