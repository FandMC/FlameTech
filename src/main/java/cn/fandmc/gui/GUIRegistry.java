package cn.fandmc.gui;

import java.util.HashMap;
import java.util.Map;

public class GUIRegistry {
    private static final Map<String, GUIConfig> registry = new HashMap<>();

    public static void register(String name, int size, String title) {
        registry.put(name.toLowerCase(), new GUIConfig(size, title));
    }

    public static GUIConfig getConfig(String name) {
        return registry.get(name.toLowerCase());
    }

    public static boolean exists(String name) {
        return registry.containsKey(name.toLowerCase());
    }

    public static class GUIConfig {
        private final int size;
        private final String title;

        public GUIConfig(int size, String title) {
            this.size = size;
            this.title = title;
        }

        public int getSize() {
            return size;
        }

        public String getTitle() {
            return title;
        }
    }
}
