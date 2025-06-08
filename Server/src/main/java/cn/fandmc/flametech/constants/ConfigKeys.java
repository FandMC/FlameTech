package cn.fandmc.flametech.constants;

public final class ConfigKeys {

    // General config
    public static final String LANGUAGE = "language";
    public static final String UPDATE_CHECKER_ENABLED = "update-checker.enabled";
    public static final String DEBUG_ENABLED = "debug.enabled";
    public static final String DEBUG_LOG_TOOL_USAGE = "debug.log_tool_usage";

    // Tool config
    public static final String TOOLS_EXPLOSIVE_ENABLED = "tools.explosive_pickaxe.enabled";
    public static final String TOOLS_EXPLOSIVE_RADIUS = "tools.explosive_pickaxe.explosion_radius";
    public static final String TOOLS_EXPLOSIVE_MAX_BLOCKS = "tools.explosive_pickaxe.max_blocks";
    public static final String TOOLS_EXPLOSIVE_MAX_DURABILITY_DAMAGE = "tools.explosive_pickaxe.max_durability_damage";
    public static final String TOOLS_EXPLOSIVE_PERMISSION_CHECK = "tools.explosive_pickaxe.permission_check";

    public static final String TOOLS_SMELTING_ENABLED = "tools.smelting_pickaxe.enabled";
    public static final String TOOLS_SMELTING_PARTICLE_EFFECTS = "tools.smelting_pickaxe.particle_effects";
    public static final String TOOLS_SMELTING_SOUND_EFFECTS = "tools.smelting_pickaxe.sound_effects";
    public static final String TOOLS_SMELTING_SUCCESS_RATE = "tools.smelting_pickaxe.success_rate";

    private ConfigKeys() {
        throw new UnsupportedOperationException("Utility class");
    }
}