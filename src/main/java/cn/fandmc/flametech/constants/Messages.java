package cn.fandmc.flametech.constants;

public final class Messages {

    // Command messages
    public static final String COMMAND_HELP_TITLE = "command.help.title";
    public static final String COMMAND_HELP_LINE1 = "command.help.line1";
    public static final String COMMAND_HELP_LINE2 = "command.help.line2";
    public static final String COMMAND_HELP_LINE3 = "command.help.line3";
    public static final String COMMAND_HELP_LINE4 = "command.help.line4";
    public static final String COMMAND_RELOAD_SUCCESS = "command.reload.success";
    public static final String COMMAND_GUIDE_SUCCESS = "command.guide.success";
    public static final String COMMAND_GUIDE_ONLY_PLAYER = "command.guide.only_player";

    // GUI messages
    public static final String GUI_COMMON_BACK = "gui.common.back";
    public static final String GUI_COMMON_NEXT_PAGE = "gui.common.next_page";
    public static final String GUI_COMMON_PREVIOUS_PAGE = "gui.common.previous_page";

    // Multiblock messages
    public static final String MULTIBLOCK_NOT_UNLOCKED = "multiblock.not_unlocked";
    public static final String MULTIBLOCK_ENHANCED_CRAFTING_CREATED = "multiblock.enhanced_crafting_table.created";
    public static final String MULTIBLOCK_ENHANCED_CRAFTING_HINT = "multiblock.enhanced_crafting_table.hint";
    public static final String MULTIBLOCK_ENHANCED_CRAFTING_ERROR = "multiblock.enhanced_crafting_table.error";
    public static final String MULTIBLOCK_ENHANCED_CRAFTING_NO_RECIPE = "multiblock.enhanced_crafting_table.no_recipe";
    public static final String MULTIBLOCK_ENHANCED_CRAFTING_INVENTORY_FULL = "multiblock.enhanced_crafting_table.inventory_full";
    public static final String MULTIBLOCK_ENHANCED_CRAFTING_CRAFT_SUCCESS = "multiblock.enhanced_crafting_table.craft_success";

    // Unlock messages
    public static final String UNLOCK_SUCCESS = "unlock.success";
    public static final String UNLOCK_INSUFFICIENT_EXP = "unlock.insufficient_exp";
    public static final String UNLOCK_ALREADY_UNLOCKED = "unlock.already_unlocked";

    // Recipe messages
    public static final String RECIPE_NOT_UNLOCKED = "recipe.not_unlocked";

    // Tool messages
    public static final String TOOLS_EXPLOSIVE_EXPLOSION = "tools.explosive_pickaxe.explosion_message";
    public static final String TOOLS_SMELTING_MESSAGE = "tools.smelting_pickaxe.smelting_message";

    private Messages() {
        throw new UnsupportedOperationException("Utility class");
    }
}