package chalkinshmeal.mc_plugin_lib.strings;

import java.util.List;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class StringUtils {
    //---------------------------------------------------------------------------------------------
    // General
    //---------------------------------------------------------------------------------------------
    public static String componentToString(Component text) {
        return LegacyComponentSerializer.legacySection().serialize(text);
    }
    
    public static Component stringToComponent(String text) {
        return Component.text(text);
    }

    public static Component componentListToComponent(List<Component> components) {
        return Component.empty().append(components);
    }
}