package chalkinshmeal.mc_plugin_lib.custom_tasks.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import chalkinshmeal.mc_plugin_lib.custom_tasks.CustomTask;
import chalkinshmeal.lockin.utils.Utils;

public class PunchPlayerWithItemTask extends CustomTask {
    private static final String configKey = "punchPlayerWithItemTask";
    private static final String normalKey = "materials";
    private final Material material;

    //---------------------------------------------------------------------------------------------
    // Constructor, which takes lockintaskhandler
    //---------------------------------------------------------------------------------------------
    public PunchPlayerWithItemTask(Material material) {
        super();
        this.material = material;
        this.description = "Punch an enemy player with a " + Utils.getReadableMaterialName(this.material);
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
		this.listeners.add(new PunchPlayerWithItemTaskPlayerInteractListener(this));
    }

    //---------------------------------------------------------------------------------------------
    // Task getter
    //---------------------------------------------------------------------------------------------
    public static List<PunchPlayerWithItemTask> getTasks(int tier) {
        List<PunchPlayerWithItemTask> tasks = new ArrayList<>();
        int taskCount = config.getInt(configKey + "." + maxTaskCount, 1);
        List<String> materialStrs = Utils.getRandomItems(config.getListFromKey(configKey + "." + normalKey + "." + tier), taskCount);
        int loopCount = Math.min(taskCount, materialStrs.size());

        Collections.shuffle(materialStrs);

        for (int i = 0; i < loopCount; i++) {
            Material material = Material.valueOf(materialStrs.get(i));
            tasks.add(new PunchPlayerWithItemTask(material));
        }
        return tasks;
    }

    //---------------------------------------------------------------------------------------------
    // Any listeners. Upon completion, lockinTaskHandler.CompleteTask(player);
    //---------------------------------------------------------------------------------------------
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager)) return;
        if (!(event.getEntity() instanceof Player victim)) return;
        if (CustomTask.teamHandler.getTeam(damager) == CustomTask.teamHandler.getTeam(victim)) return;

        Material itemInHand = damager.getInventory().getItemInMainHand().getType();
        if (itemInHand != this.material) return;

        this.complete(damager);
    }
}

//---------------------------------------------------------------------------------------------
// Private classes - any listeners that this task requires
//---------------------------------------------------------------------------------------------
class PunchPlayerWithItemTaskPlayerInteractListener implements Listener {
    private final PunchPlayerWithItemTask task;

    public PunchPlayerWithItemTaskPlayerInteractListener(PunchPlayerWithItemTask task) {
        this.task = task;
    }

    /** Event Handler */
    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (this.task.haveAllTeamsCompleted()) return;
        this.task.onEntityDamageByEntityEvent(event);
    }
}

