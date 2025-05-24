package cn.fandmc.recipe;

public enum RecipeType {
    SHAPED("有序合成"),
    SHAPELESS("无序合成"),
    SMELTING("熔炼"),
    CRUSHING("粉碎"),
    COMPRESSING("压缩");

    private final String displayName;

    RecipeType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}