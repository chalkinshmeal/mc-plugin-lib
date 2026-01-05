package chalkinshmeal.mc_plugin_lib.custom_tasks.tasks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Lectern;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import chalkinshmeal.mc_plugin_lib.custom_tasks.CustomTask;

public class PlaceBookOnLecternTask extends CustomTask {
    //---------------------------------------------------------------------------------------------
    // Constructor, which takes lockintaskhandler
    //---------------------------------------------------------------------------------------------
    public PlaceBookOnLecternTask() {
        super();
        this.description = "Place a book on a lectern";
        this.displayItem = new ItemStack(Material.LECTERN);
    }

    //---------------------------------------------------------------------------------------------
    // Abstract methods
    //---------------------------------------------------------------------------------------------
    public void validateConfig() {}

    public void addListeners() {
		this.listeners.add(new PlaceBookOnLecternTaskPlayerInteractEventListener(this));
    }

    //---------------------------------------------------------------------------------------------
    // Task getter
    //---------------------------------------------------------------------------------------------
    public static List<PlaceBookOnLecternTask> getTasks(int tier) {
        if (tier != 3) return new ArrayList<>();
        List<PlaceBookOnLecternTask> tasks = new ArrayList<>();
        tasks.add(new PlaceBookOnLecternTask());
        return tasks;
    }

    //---------------------------------------------------------------------------------------------
    // Any listeners. Upon completion, lockinTaskHandler.CompleteTask(player);
    //---------------------------------------------------------------------------------------------
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.LECTERN) return;

        // Check if the lectern is empty
        Lectern lectern = (Lectern) block.getState();
        if (lectern.getInventory().getItem(0) != null) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() != Material.WRITABLE_BOOK && item.getType() != Material.WRITTEN_BOOK) return;

        this.complete(player);
    }
}

//---------------------------------------------------------------------------------------------
// Private classes - any listeners that this task requires
//---------------------------------------------------------------------------------------------
class PlaceBookOnLecternTaskPlayerInteractEventListener implements Listener {
    private final PlaceBookOnLecternTask task;

    public PlaceBookOnLecternTaskPlayerInteractEventListener(PlaceBookOnLecternTask task) {
        this.task = task;
    }

    /** Event Handler */
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (this.task.haveAllTeamsCompleted()) return;
        this.task.onPlayerInteractEvent(event);
    }
}
