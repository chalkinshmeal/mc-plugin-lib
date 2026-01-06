package chalkinshmeal.mc_plugin_lib.custom_tasks.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import chalkinshmeal.mc_plugin_lib.custom_tasks.CustomTask;
import chalkinshmeal.lockin.utils.Utils;
import chalkinshmeal.mc_plugin_lib.logging.LoggerUtils;

public class ObtainItemWithEnchantmentTask extends CustomTask {
    private static final String configKey = "obtainItemWithEnchantmentTask";
    private static final String normalKey = "enchantments";
    private Enchantment enchantment;

    //---------------------------------------------------------------------------------------------
    // Constructor, which takes lockintaskhandler
    //---------------------------------------------------------------------------------------------
    public ObtainItemWithEnchantmentTask(Enchantment enchantment) {
        super();
        this.enchantment = enchantment;
        this.description = "Obtain an item with " + Utils.getReadableEnchantmentName(this.enchantment);
        this.displayItem = new ItemStack(Material.ENCHANTING_TABLE);
    }

    //---------------------------------------------------------------------------------------------
    // Abstract methods
    //---------------------------------------------------------------------------------------------
    public void validateConfig() {
        for (String tierStr : config.getKeyListFromKey(configKey + "." + normalKey)) {
            for (String valueStr : config.getListFromKey(configKey + "." + normalKey + "." + tierStr)) {
                Enchantment enchantment = Utils.getEnchantmentByString(valueStr);
                if (enchantment == null) {
                    LoggerUtils.info("ERROR: Invalid enchantment found: " + valueStr);
                }
            }
        }
    }

    public void addListeners() {
		this.listeners.add(new ObtainItemWithEnchantmentTaskEntityPickupItemEventListener(this));
		this.listeners.add(new ObtainItemWithEnchantmentTaskInventoryClickEventListener(this));
    }

    //---------------------------------------------------------------------------------------------
    // Task getter
    //---------------------------------------------------------------------------------------------
    public static List<ObtainItemWithEnchantmentTask> getTasks(int tier) {
        List<ObtainItemWithEnchantmentTask> tasks = new ArrayList<>();
        int taskCount = config.getInt(configKey + "." + maxTaskCount, 1);
        List<String> enchantmentStrs = Utils.getRandomItems(config.getListFromKey(configKey + "." + normalKey + "." + tier), taskCount);
        int loopCount = Math.min(taskCount, enchantmentStrs.size());
        Collections.shuffle(enchantmentStrs);

        for (int i = 0; i < Math.min(loopCount, enchantmentStrs.size()); i++) {
            Enchantment enchantment = Utils.getEnchantmentByString(enchantmentStrs.get(i));
            tasks.add(new ObtainItemWithEnchantmentTask(enchantment));
        }
        return tasks;
    }

    //---------------------------------------------------------------------------------------------
    // Any listeners. Upon completion, lockinTaskHandler.CompleteTask(player);
    //---------------------------------------------------------------------------------------------
    public void onEntityPickupItemEvent(EntityPickupItemEvent event) {
        // Return if not a player
        if (!(event.getEntity() instanceof Player player)) return;

        // Return if item doesn't contain target enchantment
        ItemStack item = event.getItem().getItemStack();
        if (!item.getEnchantments().containsKey(this.enchantment)) return;

        this.complete(player);
    }

    public void onInventoryClickEvent(InventoryClickEvent event) {
        // Return if not a player
        if (!(event.getWhoClicked() instanceof Player player)) return;

        // Return if no item clicked
        if (event.getCurrentItem() == null) return;

        // Return if item doesn't contain target enchantment
        ItemStack item = event.getCurrentItem();
        if (!item.getEnchantments().containsKey(this.enchantment)) return;

        this.complete(player);
    }
}

//---------------------------------------------------------------------------------------------
// Private classes - any listeners that this task requires
//---------------------------------------------------------------------------------------------
class ObtainItemWithEnchantmentTaskEntityPickupItemEventListener implements Listener {
    private final ObtainItemWithEnchantmentTask task;

    public ObtainItemWithEnchantmentTaskEntityPickupItemEventListener(ObtainItemWithEnchantmentTask task) {
        this.task = task;
    }

    /** Event Handler */
    @EventHandler
    public void onEntityPickupItemEvent(EntityPickupItemEvent event) {
        if (this.task.haveAllTeamsCompleted()) return;
        this.task.onEntityPickupItemEvent(event);
    }
}

class ObtainItemWithEnchantmentTaskInventoryClickEventListener implements Listener {
    private final ObtainItemWithEnchantmentTask task;

    public ObtainItemWithEnchantmentTaskInventoryClickEventListener(ObtainItemWithEnchantmentTask task) {
        this.task = task;
    }

    /** Event Handler */
    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (this.task.haveAllTeamsCompleted()) return;
        this.task.onInventoryClickEvent(event);
    }
}