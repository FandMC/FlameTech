package cn.fandmc.flametech.recipes.base;

/**
 * 配方类型枚举
 */
public enum RecipeType {
    SHAPED("有序合成", "gui.recipe_types.shaped"),
    SHAPELESS("无序合成", "gui.recipe_types.shapeless"),
    SMELTING("熔炼", "gui.recipe_types.smelting"),
    CRUSHING("粉碎", "gui.recipe_types.crushing"),
    COMPRESSING("压缩", "gui.recipe_types.compressing"),
    CUSTOM("自定义", "gui.recipe_types.custom");

    private final String displayName;
    private final String langKey;

    RecipeType(String displayName, String langKey) {
        this.displayName = displayName;
        this.langKey = langKey;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getLangKey() {
        return langKey;
    }

    /**
     * 根据名称获取配方类型
     */
    public static RecipeType fromName(String name) {
        for (RecipeType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return CUSTOM;
    }
}