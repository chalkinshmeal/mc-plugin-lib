package chalkinshmeal.mc_plugin_lib.strings;

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
}