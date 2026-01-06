package chalkinshmeal.mc_plugin_lib.custom_tasks.tasks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import chalkinshmeal.mc_plugin_lib.custom_tasks.CustomTask;
import chalkinshmeal.lockin.utils.Utils;


public class SpecificDeathTask extends CustomTask {
    private static final String configKey = "specificDeathTask";
    private static final String normalKey = "damageCauses";
    private static final String materialSubKey = "material";
    private static final String explanationSubKey = "explanation";
    private final DamageCause damageCause;
    private final Material material;

    //---------------------------------------------------------------------------------------------
    // Constructor, which takes lockintaskhandler
    //---------------------------------------------------------------------------------------------
    public SpecificDeathTask(DamageCause damageCause, Material material, String explanation) {
        super();
        this.damageCause = damageCause;
        this.material = material;
        this.description = explanation;
        this.displayItem = new ItemStack(this.material);
    }

    //---------------------------------------------------------------------------------------------
    // Abstract methods
    //---------------------------------------------------------------------------------------------
    public void validateConfig() {
        for (String tierStr : config.getKeyListFromKey(configKey + "." + normalKey)) {
            for (String valueStr : config.getListFromKey(configKey + "." + normalKey + "." + tierStr)) {
                DamageCause.valueOf(valueStr);
            }
        }
    }

    public void addListeners() {
		this.listeners.add(new SpecificDeathTaskEntityDeathEventListener(this));
    }

    //---------------------------------------------------------------------------------------------
    // Task getter
    //---------------------------------------------------------------------------------------------
    public static List<SpecificDeathTask> getTasks(int tier) {
        List<SpecificDeathTask> tasks = new ArrayList<>();
        int taskCount = config.getInt(configKey + "." + maxTaskCount, 1);
        String subKey = normalKey;
        List<String> damageCauseStrs = Utils.getRandomItems(config.getKeyListFromKey(configKey + "." + subKey + "." + tier), taskCount);
        int loopCount = Math.min(taskCount, damageCauseStrs.size());

        for (int i = 0; i < loopCount; i++) {
            String damageCauseStr = damageCauseStrs.get(i);
            DamageCause damageCause = DamageCause.valueOf(damageCauseStrs.get(i));
            Material material = config.getMaterialFromKey(configKey + "." + subKey + "." + tier + "." + damageCauseStr + "." + materialSubKey);
            String explanation = config.getString(configKey + "." + subKey + "." + tier + "." + damageCauseStr + "." + explanationSubKey, "");
            tasks.add(new SpecificDeathTask(damageCause, material, explanation));
        }
        return tasks;
    }

    //---------------------------------------------------------------------------------------------
    // Any listeners. Upon completion, lockinTaskHandler.CompleteTask(player);
    //---------------------------------------------------------------------------------------------
    public void onEntityDeathEvent(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        if (player.getLastDamageCause() == null) return;

        DamageCause cause = player.getLastDamageCause().getCause();
        if (cause != this.damageCause) return;

        this.complete(player);
    }
}

//---------------------------------------------------------------------------------------------
// Private classes - any listeners that this task requires
//---------------------------------------------------------------------------------------------
class SpecificDeathTaskEntityDeathEventListener implements Listener {
    private final SpecificDeathTask task;

    public SpecificDeathTaskEntityDeathEventListener(SpecificDeathTask task) {
        this.task = task;
    }

    /** Event Handler */
    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent event) {
        if (this.task.haveAllTeamsCompleted()) return;
        this.task.onEntityDeathEvent(event);
    }
}
