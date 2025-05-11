package cn.fandmc.config;

import cn.fandmc.Main;

public final class Config {
    // Block
    public static final String BLOCK_ENHANCEDWORKBENCH_NAME = getlang("Block.EnhancedWorkbench.Name");
    public static final String BLOCKSTRUCTURE_CREATED_SUCCESS = getlang("BlockStructure.Created.Success");
    public static final String BLOCKSTRUCTURE_ENHANCEDWORKBENCH_DESCRIPTION = getlang("BlockStructure.EnhancedWorkbench.Description");
    public static final String BLOCKSTRUCTURE_ENHANCEDWORKBENCH_TITLE = getlang("BlockStructure.EnhancedWorkbench.Title");
    public static final String BLOCKSTRUCTURE_ENHANCEDWORKBENCH_DISPENSER = getlang("BlockStructure.EnhancedWorkbench.Dispenser");

    // Item
    public static final String ITEM_BASEMACHINE_NAME = getlang("Item.BaseMachine.Name");
    public static final String ITEM_STRANGETOOL_NAME = getlang("Item.StrangeTool.Name");
    public static final String ITEM_FLAMETECHMANUAL_DISPLAYNAME = getlang("Item.FlameTechManual.DisplayName");
    public static final String ITEM_FLAMETECHMANUAL_LORE = getlang("Item.FlameTechManual.Lore");
    public static final String ITEM_SMELTINGPICKAXE_NAME = getlang("Item.SmeltingPickaxe.Name");
    public static final String ITEM_SMELTINGPICKAXE_LORE1 = getlang("Item.SmeltingPickaxe.Lore1");
    public static final String ITEM_SMELTINGPICKAXE_LORE2 = getlang("Item.SmeltingPickaxe.Lore2");
    public static final String ITEM_EXPLOSIVEPICKAXE_NAME = getlang("Item.ExplosivePickaxe.Name");
    public static final String ITEM_EXPLOSIVEPICKAXE_LORE1 = getlang("Item.ExplosivePickaxe.Lore1");
    public static final String ITEM_EXPLOSIVEPICKAXE_LORE2 = getlang("Item.ExplosivePickaxe.Lore2");

    // Commands
    public static final String COMMANDS_CONSOLE = getlang("Commands.Console");
    public static final String COMMANDS_HELPMENU = getlang("Commands.HelpMenu");
    public static final String COMMANDS_DEFAULT = getlang("Commands.default");
    public static final String COMMANDS_GIVEBOOK = getlang("Commands.giveBook");
    public static final String COMMANDS_HELP_GUIDE = getlang("Commands.help.guide");
    public static final String COMMANDS_HELP_HELP = getlang("Commands.help.help");
    public static final String COMMANDS_HELP_OPEN = getlang("Commands.help.open");
    public static final String COMMANDS_HELP_RELOAD = getlang("Commands.help.reload");
    public static final String COMMANDS_RELOAD_DONE = getlang("Commands.reload.done");

    // Crafting
    public static final String CRAFTING_SUCCESS = getlang("Crafting.Success");
    public static final String CRAFTING_ERROR_INVALIDRECIPE = getlang("Crafting.Error.InvalidRecipe");
    public static final String CRAFTING_ERROR_FULLINVENTORY = getlang("Crafting.Error.FullInventory");

    // GUI
    public static final String GUI_TOOLTIP_CLICKTOCRAFT = getlang("GUI.Tooltip.ClickToCraft");
    public static final String GUI_TOOLTIP_CLICKTOOPEN = getlang("GUI.Tooltip.ClickToOpen");
    public static final String GUI_NAME = getlang("GUI.Name");
    public static final String GUI_OPEN = getlang("Gui.Open");

    // Recipe
    public static final String RECIPE_ERROR_DUPLICATEID = getlang("Recipe.Error.DuplicateID");
    public static final String RECIPE_ERROR_INVALID = getlang("Recipe.Error.Invalid");
    public static final String RECIPE_TITLE = getlang("Recipe.Title");
    public static final String RECIPE_SMELTINGPICKAXE_NAME = getlang("Recipe.SmeltingPickaxe.Name");
    public static final String RECIPE_EXPLOSIVEPICKAXE_NAME = getlang("Recipe.ExplosivePickaxe.Name");

    private static String getlang(String config) {
        return Main.getconfig().color(config);
    }
}
