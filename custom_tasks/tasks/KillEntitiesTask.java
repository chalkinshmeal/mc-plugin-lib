package chalkinshmeal.mc_plugin_lib.custom_tasks.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import chalkinshmeal.mc_plugin_lib.custom_tasks.CustomTask;
import chalkinshmeal.lockin.utils.Utils;
import chalkinshmeal.mc_plugin_lib.teams.Team;



public class KillEntitiesTask extends CustomTask {
    private static final String configKey = "killEntitiesTask";
    private static final String normalKey = "entityTypes";
    private final EntityType entityType;
    private final int amount;
    private final Map<Team, Integer> killedEntities;

    //---------------------------------------------------------------------------------------------
    // Constructor, which takes lockintaskhandler
    //---------------------------------------------------------------------------------------------
    public KillEntitiesTask(EntityType entityType, int amount) {
        super();
        this.entityType = entityType;
        this.amount = amount;
        this.killedEntities = new HashMap<>();
        this.description = "Kill " + this.amount + " " + Utils.getReadableEntityTypeName(entityType);
        this.displayItem = new ItemStack(Material.IRON_SWORD);
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
		this.listeners.add(new KillEntitiesTaskEntityDeathEventListener(this));
    }

    //---------------------------------------------------------------------------------------------
    // Task getter
    //---------------------------------------------------------------------------------------------
    public static List<KillEntitiesTask> getTasks(int tier) {
        List<KillEntitiesTask> tasks = new ArrayList<>();
        int taskCount = config.getInt(configKey + "." + maxTaskCount, 1);
        String subKey = normalKey;
        List<String> entityTypeStrs = Utils.getRandomItems(config.getKeyListFromKey(configKey + "." + subKey + "." + tier), taskCount);
        int loopCount = Math.min(taskCount, entityTypeStrs.size());

        for (int i = 0; i < loopCount; i++) {
            String entityTypeStr = entityTypeStrs.get(i);
            EntityType entityType = EntityType.valueOf(entityTypeStrs.get(i));
            int amount = config.getInt(configKey + "." + subKey + "." + tier + "." + entityTypeStr, 1);
            tasks.add(new KillEntitiesTask(entityType, amount));
        }
        return tasks;
    }

    //---------------------------------------------------------------------------------------------
    // Any listeners. Upon completion, lockinTaskHandler.CompleteTask(player);
    //---------------------------------------------------------------------------------------------
    public void onEntityDeathEvent(EntityDeathEvent event) {
        EntityType entityType = event.getEntity().getType();
        if (entityType != this.entityType) return;

        if (!(event.getEntity().getKiller() instanceof Player)) return;
        Player player = event.getEntity().getKiller();
        Team team = CustomTask.teamHandler.getTeam(player);

        this.killedEntities.put(team, this.killedEntities.getOrDefault(team, 0) + 1);
        if (this.killedEntities.get(team) < this.amount) return;
        this.complete(player);
    }
}

//---------------------------------------------------------------------------------------------
// Private classes - any listeners that this task requires
//---------------------------------------------------------------------------------------------
class KillEntitiesTaskEntityDeathEventListener implements Listener {
    private final KillEntitiesTask task;

    public KillEntitiesTaskEntityDeathEventListener(KillEntitiesTask task) {
        this.task = task;
    }

    /** Event Handler */
    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent event) {
        if (this.task.haveAllTeamsCompleted()) return;
        this.task.onEntityDeathEvent(event);
    }
}
