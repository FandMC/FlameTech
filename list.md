# 项目结构
```
cn.fandmc.flametech/
├── Main.java                          // 插件主类，只负责初始化
├── constants/                         // 常量包
│   ├── Messages.java                  // 消息常量
│   ├── Permissions.java               // 权限常量  
│   ├── ConfigKeys.java                // 配置键常量
│   └── ItemKeys.java                  // 物品键常量
├── config/                            // 配置管理
│   └── ConfigManager.java             // 配置管理器
├── commands/                          // 命令系统
│   ├── FlameTechCommand.java          // 主命令
│   └── FlameTechTabCompleter.java     // Tab补全
├── gui/                               // GUI系统
│   ├── manager/GUIManager.java        // GUI管理器
│   ├── base/                          // 基础GUI类
│   │   ├── BaseGUI.java               // GUI基类
│   │   └── PaginatedGUI.java          // 分页GUI
│   ├── components/                    // GUI组件
│   │   ├── GUIComponent.java          // 组件接口
│   │   ├── StaticComponent.java       // 静态组件
│   │   ├── UnlockableComponent.java   // 可解锁组件
│   │   └── NavigationComponent.java   // 导航组件
│   ├── impl/                          // GUI实现
│   │   ├── MainGUI.java               // 主界面
│   │   ├── BasicMachinesGUI.java      // 基础机器界面
│   │   ├── ToolsGUI.java              // 工具界面
│   │   ├── RecipeViewerGUI.java       // 配方查看界面
│   │   ├── ItemRecipeGUI.java         // 物品配方界面
│   │   └── StructureRecipesGUI.java   // 结构配方列表
│   ├── buttons/                       // GUI按钮
│   │   ├── main/                      // 主界面按钮
│   │   ├── machines/                  // 机器按钮
│   │   └── tools/                     // 工具按钮
│   └── listeners/                     // GUI监听器
├── items/                             // 物品系统
│   ├── manager/ItemManager.java       // 物品管理器
│   ├── base/                          // 基础类
│   │   ├── CustomItem.java            // 自定义物品基类
│   │   └── SpecialTool.java           // 特殊工具基类
│   ├── tools/                         // 工具实现
│   │   ├── ExplosivePickaxe.java      // 爆炸镐
│   │   └── SmeltingPickaxe.java       // 熔炼镐
│   └── builders/                      // 构建器
│       └── ItemBuilder.java           // 物品构建器
├── recipes/                           // 配方系统
│   ├── manager/RecipeManager.java     // 配方管理器
│   ├── base/                          // 基础类
│   │   ├── Recipe.java                // 配方基类
│   │   ├── ShapedRecipe.java          // 有序配方
│   │   └── RecipeType.java            // 配方类型
│   └── impl/                          // 配方实现
│       └── ToolRecipes.java           // 工具配方
├── multiblock/                        // 多方块系统
│   ├── manager/MultiblockManager.java // 多方块管理器
│   ├── base/                          // 基础类
│   │   ├── MultiblockStructure.java   // 多方块基类
│   │   └── BlockOffset.java           // 方块偏移
│   └── impl/                          // 多方块实现
│       └── EnhancedCraftingTable.java // 增强工作台
├── unlock/                            // 解锁系统
│   ├── manager/UnlockManager.java     // 解锁管理器
│   └── data/                          // 数据类
│       ├── PlayerUnlocks.java         // 玩家解锁数据
│       ├── UnlockableItem.java        // 可解锁物品
│       └── UnlockResult.java          // 解锁结果
├── listeners/                         // 事件监听器
│   ├── PlayerInteractListener.java    // 玩家交互监听
│   ├── BlockBreakListener.java        // 方块破坏监听
│   └── GUIListener.java               // GUI监听
├── utils/                             // 工具类
│   ├── ItemUtils.java                 // 物品工具
│   ├── MessageUtils.java              // 消息工具
│   ├── ValidationUtils.java           // 验证工具
│   ├── LocationUtils.java             // 位置工具
│   ├── BookUtils.java                 // 书籍工具
│   └── FileUtils.java                 // 文件工具
└── exceptions/                        // 自定义异常
    ├── FlameTechException.java        // 基础异常
    ├── RecipeException.java           // 配方异常
    └── MultiblockException.java       // 多方块异常