package cn.fandmc.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUIRegistry {
    static final Map<String, Page> pages = new HashMap<>();
    private static final Map<Class<?>, String> componentPageMap = new HashMap<>();

    public static void registerPage(String pageId, String title, int size, boolean autoArrange) {
        pages.put(pageId, new Page(pageId, title, size, autoArrange));
    }

    public static void registerComponent(String pageId, GUIComponent component) {
        Page page = pages.get(pageId);
        if (page != null) {
            page.addComponent(component);
            componentPageMap.put(component.getClass(), pageId);
        }
    }

    public static Page getPage(String pageId) {
        return pages.get(pageId);
    }

    public static String getComponentPage(Class<?> componentClass) {
        return componentPageMap.get(componentClass);
    }

    public static class Page {
        private final String id;
        private final String title;
        private final int size;
        private final boolean autoArrange;
        private final List<GUIComponent> components = new ArrayList<>();

        public Page(String id, String title, int size, boolean autoArrange) {
            this.id = id;
            this.title = title;
            this.size = size;
            this.autoArrange = autoArrange;
        }

        public void addComponent(GUIComponent component) {
            components.add(component);
        }

        // Getters
        public String getId() { return id; }
        public String getTitle() { return title; }
        public int getSize() { return size; }
        public boolean isAutoArrange() { return autoArrange; }
        public List<GUIComponent> getComponents() { return components; }
    }
}
