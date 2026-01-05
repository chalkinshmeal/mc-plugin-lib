package chalkinshmeal.mc_plugin_lib.custom_tasks.tasks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import chalkinshmeal.mc_plugin_lib.custom_tasks.CustomTask;
import chalkinshmeal.lockin.utils.Utils;

public class EnterBiomeTask extends CustomTask {
    private static final String configKey = "enterBiomeTask";
    private static final String normalKey = "biomes";
    private final Biome biome;

    //---------------------------------------------------------------------------------------------
    // Constructor, which takes lockintaskhandler
    //---------------------------------------------------------------------------------------------
    public EnterBiomeTask(Biome biome, Material material) {
        super();
        this.biome = biome;
        this.description = "Enter a " + Utils.getReadableBiomeName(biome) + " biome";
        this.displayItem = new ItemStack(material);
    }

    //---------------------------------------------------------------------------------------------
    // Abstract methods
    //---------------------------------------------------------------------------------------------
    @SuppressWarnings("removal")
    public void validateConfig() {
        for (String tierStr : config.getKeyListFromKey(configKey + "." + normalKey)) {
            for (String valueStr : config.getListFromKey(configKey + "." + normalKey + "." + tierStr)) {
                Material.valueOf(valueStr);
                Biome.valueOf(valueStr);
                String materialStr = config.getString(configKey + "." + normalKey + "." + valueStr, "NotAvailable");
                Material.valueOf(materialStr);
            }
        }
    }

    public void addListeners() {
		this.listeners.add(new EnterBiomeTaskPlayerItemConsumeListener(this));
    }

    //---------------------------------------------------------------------------------------------
    // Task getter
    //---------------------------------------------------------------------------------------------
    @SuppressWarnings("removal")
    public static List<EnterBiomeTask> getTasks(int tier) {
        List<EnterBiomeTask> tasks = new ArrayList<>();
        int taskCount = config.getInt(configKey + "." + maxTaskCount, 1);
        String subKey = normalKey;
        List<String> biomeStrs = Utils.getRandomItems(config.getKeyListFromKey(configKey + "." + subKey + "." + tier), taskCount);
        int loopCount = Math.min(taskCount, biomeStrs.size());

        for (int i = 0; i < loopCount; i++) {
            Biome biome = Biome.valueOf(biomeStrs.get(i));
            Material material = Material.valueOf(config.getString(configKey + "." + subKey + "." + tier + "." + biomeStrs.get(i), "NotAvailable"));
            tasks.add(new EnterBiomeTask(biome, material));
        }
        return tasks;
    }

    //---------------------------------------------------------------------------------------------
    // Any listeners. Upon completion, lockinTaskHandler.CompleteTask(player);
    //---------------------------------------------------------------------------------------------
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Biome currentBiome = player.getLocation().getBlock().getBiome();
        if (currentBiome != this.biome) return;

        this.complete(event.getPlayer());
    }
}

//---------------------------------------------------------------------------------------------
// Private classes - any listeners that this task requires
//---------------------------------------------------------------------------------------------
class EnterBiomeTaskPlayerItemConsumeListener implements Listener {
    private final EnterBiomeTask task;

    public EnterBiomeTaskPlayerItemConsumeListener(EnterBiomeTask task) {
        this.task = task;
    }

    /** Event Handler */
    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        if (this.task.haveAllTeamsCompleted()) return;
        this.task.onPlayerMoveEvent(event);
    }
}

