package chalkinshmeal.mc_plugin_lib.custom_tasks.tasks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import chalkinshmeal.mc_plugin_lib.custom_tasks.CustomTask;
import chalkinshmeal.lockin.utils.Utils;

public class ReceivePotionEffectTypeTask extends CustomTask {
    private static final String configKey = "receivePotionEffectTypeTask";
    private static final String normalKey = "potionEffectTypes";
    private final PotionEffectType potionEffectType;

    //---------------------------------------------------------------------------------------------
    // Constructor, which takes lockintaskhandler
    //---------------------------------------------------------------------------------------------
    public ReceivePotionEffectTypeTask(PotionEffectType potionEffectType) {
        super();
        this.potionEffectType = potionEffectType;
        this.description = "Receive the " + Utils.getReadablePotionEffectTypeName(this.potionEffectType) + " potion effect";
        this.displayItem = new ItemStack(Utils.getSplashPotionFromPotionEffectType(potionEffectType));
    }

    //---------------------------------------------------------------------------------------------
    // Abstract methods
    //---------------------------------------------------------------------------------------------
    public void validateConfig() {
        for (String tierStr : config.getKeyListFromKey(configKey + "." + normalKey)) {
            for (String valueStr : config.getListFromKey(configKey + "." + normalKey + "." + tierStr)) {
                Utils.getPotionEffectTypeFromStr(valueStr);
            }
        }
    }

    public void addListeners() {
		this.listeners.add(new ReceivePotionEffectTypeTaskPlayerCraftListener(this));
    }

    //---------------------------------------------------------------------------------------------
    // Task getter
    //---------------------------------------------------------------------------------------------
    public static List<ReceivePotionEffectTypeTask> getTasks(int tier) {
        List<ReceivePotionEffectTypeTask> tasks = new ArrayList<>();
        int taskCount = config.getInt(configKey + "." + maxTaskCount, 1);
        List<String> potionEffectTypeStrs = Utils.getRandomItems(config.getListFromKey(configKey + "." + normalKey + "." + tier), taskCount);
        int loopCount = Math.min(taskCount, potionEffectTypeStrs.size());

        for (int i = 0; i < loopCount; i++) {
            PotionEffectType potionEffectType = Utils.getPotionEffectTypeFromStr(potionEffectTypeStrs.get(i));
            tasks.add(new ReceivePotionEffectTypeTask(potionEffectType));
        }
        return tasks;
    }

    //---------------------------------------------------------------------------------------------
    // Any listeners. Upon completion, lockinTaskHandler.CompleteTask(player);
    //---------------------------------------------------------------------------------------------
    public void onEntityPotionEffectEvent(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        // Check if the event is caused by the player receiving a new effect
        if (event.getNewEffect() == null || event.getAction() != EntityPotionEffectEvent.Action.ADDED) return;

        PotionEffect newEffect = event.getNewEffect();
        if (newEffect.getType() != this.potionEffectType) return;

        this.complete(player);
    }
}

//---------------------------------------------------------------------------------------------
// Private classes - any listeners that this task requires
//---------------------------------------------------------------------------------------------
class ReceivePotionEffectTypeTaskPlayerCraftListener implements Listener {
    private final ReceivePotionEffectTypeTask task;

    public ReceivePotionEffectTypeTaskPlayerCraftListener(ReceivePotionEffectTypeTask task) {
        this.task = task;
    }

    /** Event Handler */
    @EventHandler
    public void onEntityPotionEffectEvent(EntityPotionEffectEvent event) {
        if (this.task.haveAllTeamsCompleted()) return;
        this.task.onEntityPotionEffectEvent(event);
    }
}

