package chalkinshmeal.mc_plugin_lib.strings;

import java.util.Arrays;
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

    //---------------------------------------------------------------------------------------------
    // Grammar 
    //---------------------------------------------------------------------------------------------
    public static String applyAAnRules(String string) {
        // Replace 1 -> a
        if (string.contains(" 1 ")) {
            string = string.replace(" 1 ", " a ");
        }

        // Replace a -> an, if necessary
        if (string.contains(" a ")) {
            String regexPattern = "^[aeiou].*"; 
            List<String> stringList = Arrays.asList(string.split(" "));
            for (int i = 0; i < stringList.size() - 1; i++) {
                if (stringList.get(i).equals("a") && stringList.get(i + 1).matches(regexPattern)) {
                    stringList.set(i, "an");
                }
            }
            string = String.join(" ", stringList);
        }
        return string;
    }
}