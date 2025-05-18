package cn.fandmc.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class GUIRegistry {
    private static final Map<String, Supplier<GUI>> registry = new HashMap<>();

    public static void register(String key, Supplier<GUI> guiSupplier) {
        registry.put(key.toLowerCase(), guiSupplier);
    }

    public static GUI getGUI(String key) {
        Supplier<GUI> supplier = registry.get(key.toLowerCase());
        return supplier != null ? supplier.get() : null;
    }

    public static void init() {
        register("main", MainGUI::new);
    }
}
