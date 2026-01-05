package chalkinshmeal.mc_plugin_lib.custom_tasks.tasks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import chalkinshmeal.mc_plugin_lib.custom_tasks.CustomTask;
import chalkinshmeal.lockin.utils.Utils;

public class ShootBlockTask extends CustomTask {
    private static final String configKey = "shootBlockTask";
    private static final String normalKey = "materials";
    private final Material material;

    //---------------------------------------------------------------------------------------------
    // Constructor, which takes lockintaskhandler
    //---------------------------------------------------------------------------------------------
    public ShootBlockTask(Material material) {
        super();
        this.material = material;
        this.description = "Hit a " + Utils.getReadableMaterialName(material) + " with an arrow";
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
		this.listeners.add(new ShootBlockTaskPlayerItemConsumeListener(this));
    }

    //---------------------------------------------------------------------------------------------
    // Task getter
    //---------------------------------------------------------------------------------------------
    public static List<ShootBlockTask> getTasks(int tier) {
        List<ShootBlockTask> tasks = new ArrayList<>();
        int taskCount = config.getInt(configKey + "." + maxTaskCount, 1);
        List<String> materialStrs = Utils.getRandomItems(config.getListFromKey(configKey + "." + normalKey + "." + tier), taskCount);
        int loopCount = Math.min(taskCount, materialStrs.size());

        for (int i = 0; i < loopCount; i++) {
            Material material = Material.valueOf(materialStrs.get(i));
            tasks.add(new ShootBlockTask(material));
        }
        return tasks;
    }

    //---------------------------------------------------------------------------------------------
    // Any listeners. Upon completion, lockinTaskHandler.CompleteTask(player);
    //---------------------------------------------------------------------------------------------
    public void onProjectileHitEvent(ProjectileHitEvent event) {
        if (event.getHitBlock() == null) return;
        if (event.getHitBlock().getType() != this.material) return;

        Projectile projectile = event.getEntity();
        if (!(projectile instanceof Arrow)) return;

        ProjectileSource shooter = projectile.getShooter();
        if (!(shooter instanceof Player)) return;

        Player player = (Player) shooter;
        this.complete(player);
    }
}

//---------------------------------------------------------------------------------------------
// Private classes - any listeners that this task requires
//---------------------------------------------------------------------------------------------
class ShootBlockTaskPlayerItemConsumeListener implements Listener {
    private final ShootBlockTask task;

    public ShootBlockTaskPlayerItemConsumeListener(ShootBlockTask task) {
        this.task = task;
    }

    /** Event Handler */
    @EventHandler
    public void onProjectileHitEvent(ProjectileHitEvent event) {
        if (this.task.haveAllTeamsCompleted()) return;
        this.task.onProjectileHitEvent(event);
    }
}

