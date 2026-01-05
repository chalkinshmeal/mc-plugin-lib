package chalkinshmeal.mc_plugin_lib.items;

import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;

public class ItemStackUtils {
    public static ItemStack setDisplayName(ItemStack item, Component displayName) {
        ItemMeta meta = item.getItemMeta();
        meta.displayName(displayName);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack setLore(ItemStack item, List<Component> lines) {
        ItemMeta meta = item.getItemMeta();
        meta.lore(lines);
        item.setItemMeta(meta);
        return item;
    }
}