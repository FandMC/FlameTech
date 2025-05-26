package cn.fandmc.flametech.constants;

public final class Permissions {

    // Base permissions
    public static final String BASE = "flametech";
    public static final String ADMIN = "flametech.admin";
    public static final String USE = "flametech.use";

    // Command permissions
    public static final String COMMAND_HELP = "flametech.command.help";
    public static final String COMMAND_GUIDE = "flametech.command.guide";
    public static final String COMMAND_OPEN = "flametech.command.open";
    public static final String COMMAND_RELOAD = "flametech.command.reload";

    // Tool permissions
    public static final String TOOLS_EXPLOSIVE_PICKAXE = "flametech.tools.explosive_pickaxe";
    public static final String TOOLS_SMELTING_PICKAXE = "flametech.tools.smelting_pickaxe";

    // Multiblock permissions
    public static final String MULTIBLOCK_ENHANCED_CRAFTING = "flametech.multiblock.enhanced_crafting";

    private Permissions() {
        throw new UnsupportedOperationException("Utility class");
    }
}