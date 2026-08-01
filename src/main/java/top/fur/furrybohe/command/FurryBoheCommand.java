package top.fur.furrybohe.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.fur.furrybohe.Furry_bohe;
import top.fur.furrybohe.base.BaseAffix;
import top.fur.furrybohe.capability.FurArmorCapability;
import top.fur.furrybohe.capability.FurArmorCapabilityProvider;
import top.fur.furrybohe.register.RegisterAffixs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FurryBoheCommand {

    // 自动补全提供者：返回所有已注册的词条ID
    private static final SuggestionProvider<CommandSourceStack> AFFIX_SUGGESTIONS =
            (context, builder) -> {
                List<String> suggestions = new ArrayList<>();
                for (Map.Entry<ResourceLocation, BaseAffix> entry : RegisterAffixs.getAll().entrySet()) {
                    suggestions.add(entry.getKey().getPath());
                }
                return SharedSuggestionProvider.suggest(suggestions, builder);
            };

    // 自动补全提供者：返回可用的槽位
    private static final SuggestionProvider<CommandSourceStack> SLOT_SUGGESTIONS =
            (context, builder) -> SharedSuggestionProvider.suggest(new String[]{
                    "head", "chest", "legs", "feet", "offhand", "mainhand"
            }, builder);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("furrybohe")
                .requires(source -> source.hasPermission(2)) // 需要 OP 权限

                // ===== test 子命令 =====
                .then(Commands.literal("test")
                        .then(Commands.literal("affix")
                                .then(Commands.literal("add")
                                        // /furrybohe test affix add <槽位> <词条ID>
                                        .then(Commands.argument("slot", StringArgumentType.string())
                                                .suggests(SLOT_SUGGESTIONS)
                                                .then(Commands.argument("affixId", StringArgumentType.string())
                                                        .suggests(AFFIX_SUGGESTIONS)
                                                        .executes(context -> {
                                                            Player player = context.getSource().getPlayerOrException();
                                                            String slot = StringArgumentType.getString(context, "slot");
                                                            String affixId = StringArgumentType.getString(context, "affixId");
                                                            return addAffixToSlot(player, slot, affixId);
                                                        })
                                                )
                                        )
                                )
                                .then(Commands.literal("remove")
                                        // /furrybohe test affix remove <槽位> <词条ID>
                                        .then(Commands.argument("slot", StringArgumentType.string())
                                                .suggests(SLOT_SUGGESTIONS)
                                                .then(Commands.argument("affixId", StringArgumentType.string())
                                                        .suggests(AFFIX_SUGGESTIONS)
                                                        .executes(context -> {
                                                            Player player = context.getSource().getPlayerOrException();
                                                            String slot = StringArgumentType.getString(context, "slot");
                                                            String affixId = StringArgumentType.getString(context, "affixId");
                                                            return removeAffixFromSlot(player, slot, affixId);
                                                        })
                                                )
                                        )
                                )
                                .then(Commands.literal("list")
                                        // /furrybohe test affix list <槽位>
                                        .then(Commands.argument("slot", StringArgumentType.string())
                                                .suggests(SLOT_SUGGESTIONS)
                                                .executes(context -> {
                                                    Player player = context.getSource().getPlayerOrException();
                                                    String slot = StringArgumentType.getString(context, "slot");
                                                    return listAffixesInSlot(player, slot);
                                                })
                                        )
                                )
                                .then(Commands.literal("clear")
                                        // /furrybohe test affix clear <槽位>
                                        .then(Commands.argument("slot", StringArgumentType.string())
                                                .suggests(SLOT_SUGGESTIONS)
                                                .executes(context -> {
                                                    Player player = context.getSource().getPlayerOrException();
                                                    String slot = StringArgumentType.getString(context, "slot");
                                                    return clearAffixesInSlot(player, slot);
                                                })
                                        )
                                )
                        )
                )

                // ===== 快捷命令 =====
                .then(Commands.literal("affix")
                        .then(Commands.literal("list")
                                .executes(context -> {
                                    Player player = context.getSource().getPlayerOrException();
                                    return listAllAvailableAffixes(player);
                                })
                        )
                )
        );
    }

    // ======================== 核心方法 ========================

    /**
     * 给指定槽位的物品添加词条
     */
    private static int addAffixToSlot(Player player, String slotName, String affixId) {
        // 1. 解析槽位
        EquipmentSlot slot = parseSlot(slotName);
        if (slot == null) {
            player.sendSystemMessage(Component.literal("§c无效的槽位！可用: head, chest, legs, feet, offhand, mainhand"));
            return 0;
        }

        // 2. 获取物品
        ItemStack item = getItemInSlot(player, slot);
        if (item.isEmpty()) {
            player.sendSystemMessage(Component.literal("§c该槽位没有物品！"));
            return 0;
        }

        // 3. 获取词条
        BaseAffix affix = RegisterAffixs.get(affixId);
        if (affix == null) {
            player.sendSystemMessage(Component.literal("§c未找到词条: " + affixId));
            listAllAvailableAffixes(player);
            return 0;
        }
        FurArmorCapability cap = FurArmorCapabilityProvider.get(item);
        if (cap == null) {
            cap = new FurArmorCapability(item);
            CompoundTag nbt = cap.serializeNBT();
            item.getOrCreateTag().put(FurArmorCapabilityProvider.ID.toString(), nbt);

            player.sendSystemMessage(Component.literal("§a已为物品创建词条系统！"));
        }

        // 5. 检查是否已有该词条
        if (cap.hasAffix(affix)) {
            player.sendSystemMessage(Component.literal("§e该物品已有词条: " + affix.getDisplayName()));
            return 0;
        }

        // 6. 添加词条
        cap.addAffix(affix, 1);
        player.sendSystemMessage(Component.literal("§a已添加词条: §f" + affix.getDisplayName()));

        // 显示当前所有词条
        listAffixesInSlot(player, slotName);

        return 1;
    }

    /**
     * 从指定槽位移除词条
     */
    private static int removeAffixFromSlot(Player player, String slotName, String affixId) {
        EquipmentSlot slot = parseSlot(slotName);
        if (slot == null) {
            player.sendSystemMessage(Component.literal("§c无效的槽位！"));
            return 0;
        }

        ItemStack item = getItemInSlot(player, slot);
        if (item.isEmpty()) {
            player.sendSystemMessage(Component.literal("§c该槽位没有物品！"));
            return 0;
        }

        BaseAffix affix = RegisterAffixs.get(affixId);
        if (affix == null) {
            player.sendSystemMessage(Component.literal("§c未找到词条: " + affixId));
            return 0;
        }

        FurArmorCapability cap = FurArmorCapabilityProvider.get(item);
        if (cap == null) {
            player.sendSystemMessage(Component.literal("§c该物品还没有词条系统！"));
            return 0;
        }

        if (!cap.hasAffix(affix)) {
            player.sendSystemMessage(Component.literal("§e该物品没有此词条: " + affix.getDisplayName()));
            return 0;
        }

        cap.removeAffix(affix);
        player.sendSystemMessage(Component.literal("§a已移除词条: §f" + affix.getDisplayName()));

        listAffixesInSlot(player, slotName);
        return 1;
    }

    /**
     * 列出指定槽位的所有词条
     */
    private static int listAffixesInSlot(Player player, String slotName) {
        EquipmentSlot slot = parseSlot(slotName);
        if (slot == null) {
            player.sendSystemMessage(Component.literal("§c无效的槽位！"));
            return 0;
        }

        ItemStack item = getItemInSlot(player, slot);
        if (item.isEmpty()) {
            player.sendSystemMessage(Component.literal("§c该槽位没有物品！"));
            return 0;
        }

        FurArmorCapability cap = FurArmorCapabilityProvider.get(item);
        if (cap == null) {
            player.sendSystemMessage(Component.literal("§e该物品还没有词条系统"));
            return 0;
        }

        Map<ResourceLocation, Integer> affixes = cap.getAffixes();
        if (affixes.isEmpty()) {
            player.sendSystemMessage(Component.literal("§e该物品没有任何词条"));
            return 0;
        }

        player.sendSystemMessage(Component.literal("§6=== 词条列表 (" + slotName + ") ==="));
        for (Map.Entry<ResourceLocation, Integer> entry : affixes.entrySet()) {
            BaseAffix affix = RegisterAffixs.get(entry.getKey());
            if (affix != null) {
                String levelStr = entry.getValue() > 1 ? " §fLv." + entry.getValue() : "";
                player.sendSystemMessage(Component.literal(
                        "§7- " + affix.getDisplayName() + levelStr
                ));
            }
        }
        return 1;
    }

    /**
     * 清空指定槽位的所有词条
     */
    private static int clearAffixesInSlot(Player player, String slotName) {
        EquipmentSlot slot = parseSlot(slotName);
        if (slot == null) {
            player.sendSystemMessage(Component.literal("§c无效的槽位！"));
            return 0;
        }

        ItemStack item = getItemInSlot(player, slot);
        if (item.isEmpty()) {
            player.sendSystemMessage(Component.literal("§c该槽位没有物品！"));
            return 0;
        }

        FurArmorCapability cap = FurArmorCapabilityProvider.get(item);
        if (cap == null) {
            player.sendSystemMessage(Component.literal("§e该物品还没有词条系统"));
            return 0;
        }

        cap.clearAffixes();
        player.sendSystemMessage(Component.literal("§a已清空所有词条"));
        return 1;
    }

    /**
     * 列出所有已注册的词条
     */
    private static int listAllAvailableAffixes(Player player) {
        Map<ResourceLocation, BaseAffix> allAffixes = RegisterAffixs.getAll();

        if (allAffixes.isEmpty()) {
            player.sendSystemMessage(Component.literal("§e暂无可用词条"));
            return 0;
        }

        player.sendSystemMessage(Component.literal("§6=== 所有可用词条 ==="));

        // 按类型分组显示
        player.sendSystemMessage(Component.literal("§a=== 正面词条 ==="));
        for (Map.Entry<ResourceLocation, BaseAffix> entry : allAffixes.entrySet()) {
            BaseAffix affix = entry.getValue();
            if (affix.getType() == BaseAffix.AffixType.POSITIVE) {
                player.sendSystemMessage(Component.literal(
                        "§7- §f" + affix.getId() + " §7(" + affix.getDisplayName() + "§7)"
                ));
            }
        }

        player.sendSystemMessage(Component.literal("§c=== 负面词条 ==="));
        for (Map.Entry<ResourceLocation, BaseAffix> entry : allAffixes.entrySet()) {
            BaseAffix affix = entry.getValue();
            if (affix.getType() == BaseAffix.AffixType.NEGATIVE) {
                player.sendSystemMessage(Component.literal(
                        "§7- §f" + affix.getId() + " §7(" + affix.getDisplayName() + "§7)"
                ));
            }
        }

        player.sendSystemMessage(Component.literal("§d=== 特殊词条 ==="));
        for (Map.Entry<ResourceLocation, BaseAffix> entry : allAffixes.entrySet()) {
            BaseAffix affix = entry.getValue();
            if (affix.getType() == BaseAffix.AffixType.SPECIAL) {
                player.sendSystemMessage(Component.literal(
                        "§7- §f" + affix.getId() + " §7(" + affix.getDisplayName() + "§7)"
                ));
            }
        }

        return 1;
    }

    // ======================== 工具方法 ========================

    /**
     * 解析槽位名称
     */
    private static EquipmentSlot parseSlot(String slotName) {
        return switch (slotName.toLowerCase()) {
            case "head" -> EquipmentSlot.HEAD;
            case "chest" -> EquipmentSlot.CHEST;
            case "legs" -> EquipmentSlot.LEGS;
            case "feet" -> EquipmentSlot.FEET;
            case "offhand" -> EquipmentSlot.OFFHAND;
            case "mainhand" -> EquipmentSlot.MAINHAND;
            default -> null;
        };
    }

    /**
     * 获取指定槽位的物品
     */
    private static ItemStack getItemInSlot(Player player, EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> player.getInventory().armor.get(3);
            case CHEST -> player.getInventory().armor.get(2);
            case LEGS -> player.getInventory().armor.get(1);
            case FEET -> player.getInventory().armor.get(0);
            case OFFHAND -> player.getOffhandItem();
            case MAINHAND -> player.getMainHandItem();
        };
    }
}