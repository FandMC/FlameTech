package cn.fandmc.gui.impl;

import cn.fandmc.Main;
import cn.fandmc.gui.GUIComponent;
import cn.fandmc.gui.GUIManager;
import cn.fandmc.gui.templates.PaginatedGUI;
import cn.fandmc.recipe.Recipe;
import cn.fandmc.recipe.RecipeManager;
import cn.fandmc.unlock.UnlockManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructureRecipesGUI extends PaginatedGUI {
    private final String multiblockId;
    private static final Map<String, StructureRecipesGUI> instances = new HashMap<>();

    private StructureRecipesGUI(Main plugin, String multiblockId) {
        super(plugin, "structure_recipes_" + multiblockId,
                plugin.getConfigManager().getLang("gui.structure_recipes.title")
                        .replace("%machine%", plugin.getConfigManager().getLang("multiblock." + multiblockId + ".name")));
        this.multiblockId = multiblockId;
    }

    public static StructureRecipesGUI getInstance(Main plugin, String multiblockId) {
        String key = multiblockId;
        if (!instances.containsKey(key)) {
            StructureRecipesGUI gui = new StructureRecipesGUI(plugin, multiblockId);
            instances.put(key, gui);
            GUIManager.registerGUI(gui);
        }
        return instances.get(key);
    }

    @Override
    public void open(Player player) {
        // 每次打开时重新加载配方，以便根据玩家的解锁状态显示
        loadRecipesForPlayer(player);
        super.open(player);
    }

    private void loadRecipesForPlayer(Player player) {
        clearPageItems();
        List<Recipe> recipes = RecipeManager.getInstance().getRecipesForMultiblock(multiblockId);

        if (recipes.isEmpty()) {
            addPageItem(new NoRecipeItem());
        } else {
            for (Recipe recipe : recipes) {
                addPageItem(new RecipeItem(recipe, player));
            }
        }
    }

    @Override
    protected void setupControlButtons() {
        super.setupControlButtons();

        setComponent(45, new GUIComponent() {
            @Override
            public ItemStack item() {
                ItemStack item = new ItemStack(Material.ARROW);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(plugin.getConfigManager().getLang("gui.common.back"));
                    item.setItemMeta(meta);
                }
                return item;
            }

            @Override
            public void onClick(Player player, InventoryClickEvent event) {
                RecipeViewerGUI viewerGUI = RecipeViewerGUI.getInstance(plugin, multiblockId);
                viewerGUI.open(player);
            }
        });
    }

    private class NoRecipeItem implements GUIComponent {
        @Override
        public ItemStack item() {
            ItemStack item = new ItemStack(Material.BARRIER);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(plugin.getConfigManager().getLang("gui.structure_recipes.no_recipes"));
                meta.setLore(plugin.getConfigManager().getStringList("gui.structure_recipes.no_recipes_lore"));
                item.setItemMeta(meta);
            }
            return item;
        }

        @Override
        public void onClick(Player player, InventoryClickEvent event) {
        }
    }

    private class RecipeItem implements GUIComponent {
        private final Recipe recipe;
        private final Player player;

        public RecipeItem(Recipe recipe, Player player) {
            this.recipe = recipe;
            this.player = player;
        }

        @Override
        public ItemStack item() {
            String recipeUnlockId = "recipe." + recipe.getId();
            boolean isUnlocked = UnlockManager.getInstance().isUnlocked(player, recipeUnlockId);

            if (!isUnlocked) {
                // 未解锁状态 - 显示为锁定
                ItemStack lockedItem = new ItemStack(Material.BARRIER);
                ItemMeta meta = lockedItem.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName("§c" + recipe.getDisplayName() + " §7(未解锁)");
                    List<String> lore = new ArrayList<>();
                    lore.add("§7需要经验等级: §e" + UnlockManager.getInstance().getRequiredExp(recipeUnlockId));
                    lore.add("");
                    lore.add("§e点击解锁");
                    meta.setLore(lore);
                    lockedItem.setItemMeta(meta);
                }
                return lockedItem;
            } else {
                // 已解锁状态 - 显示配方结果
                ItemStack display = recipe.getResult().clone();
                ItemMeta meta = display.getItemMeta();
                if (meta != null) {
                    List<String> lore = meta.getLore();
                    if (lore == null) lore = new ArrayList<>();

                    lore.add("");
                    lore.add(plugin.getConfigManager().getLang("gui.structure_recipes.click_view_recipe"));

                    meta.setLore(lore);
                    display.setItemMeta(meta);
                }
                return display;
            }
        }

        @Override
        public void onClick(Player player, InventoryClickEvent event) {
            String recipeUnlockId = "recipe." + recipe.getId();

            if (!UnlockManager.getInstance().isUnlocked(player, recipeUnlockId)) {
                // 尝试解锁配方
                UnlockManager.UnlockResult result = UnlockManager.getInstance().unlock(player, recipeUnlockId);

                if (result.isSuccess()) {
                    String itemName = recipe.getDisplayName();
                    player.sendMessage(Main.getInstance().getConfigManager().getLang("unlock.success")
                            .replace("%item%", itemName));

                    // 刷新界面
                    StructureRecipesGUI.this.open(player);
                } else {
                    // 解锁失败
                    switch (result.getMessage()) {
                        case "insufficient_exp":
                            player.sendMessage(Main.getInstance().getConfigManager().getLang("unlock.insufficient_exp")
                                    .replace("%required%", String.valueOf(result.getRequiredExp())));
                            break;
                        case "already_unlocked":
                            player.sendMessage(Main.getInstance().getConfigManager().getLang("unlock.already_unlocked"));
                            break;
                        default:
                            player.sendMessage("§c解锁失败: " + result.getMessage());
                            break;
                    }
                }
            } else {
                // 已解锁，打开配方详情
                ItemRecipeGUI recipeGUI = new ItemRecipeGUI(plugin, recipe, multiblockId);
                recipeGUI.open(player);
            }
        }
    }
}