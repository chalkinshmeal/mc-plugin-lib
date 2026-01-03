package chalkinshmeal.mc_plugin_lib.items;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import net.kyori.adventure.text.Component;

public class BookUtils {
    public static ItemStack createBook(List<Component> pages, Component title, Component author) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();

        meta.title(title);
        meta.author(author);
        meta.pages(pages);

        book.setItemMeta(meta);
        return book;
    }
}