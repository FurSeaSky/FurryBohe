package top.fur.furrybohe.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import top.fur.furrybohe.Furry_bohe;
import top.fur.furrybohe.base.BaseAffix;
import top.fur.furrybohe.capability.FurArmorCapability;
import top.fur.furrybohe.capability.FurArmorCapabilityProvider;
import top.fur.furrybohe.capability.PlayerCapability;
import top.fur.furrybohe.register.RegisterAffixs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 统一命令注册类
 * 整合了 fursuit_maker_xp 和 affix 测试命令
 */
public class ModCommands {

    // ======================== 自动补全提供者 ========================

    private static final SuggestionProvider<CommandSourceStack> AFFIX_SUGGESTIONS =
            (context, builder) -> {
                List<String> suggestions = new ArrayList<>();
                for (Map.Entry<ResourceLocation, BaseAffix> entry : RegisterAffixs.getAll().entrySet()) {
                    suggestions.add(entry.getKey().getPath());
                }
                return SharedSuggestionProvider.suggest(suggestions, builder);
            };

    private static final SuggestionProvider<CommandSourceStack> SLOT_SUGGESTIONS =
            (context, builder) -> SharedSuggestionProvider.suggest(new String[]{
                    "head", "chest", "legs", "feet", "offhand", "mainhand"
            }, builder);

    // ======================== 主注册方法 ========================

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("furrybohe")
                .requires(source -> source.hasPermission(2)) // 需要 OP 权限

                // ===== fursuit_maker_xp 子命令（来自原 ModCommands） =====
                .then(Commands.literal("fursuit_maker_xp")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.literal("add")
                                        .then(Commands.argument("xp", IntegerArgumentType.integer())
                                                .executes(ModCommands::addFursuitMakerXp)
                                        )
                                )
                                .then(Commands.literal("set")
                                        .then(Commands.argument("xp", IntegerArgumentType.integer())
                                                .executes(ModCommands::setFursuitMakerXp)
                                        )
                                )
                        )
                )

                // ===== test affix 子命令（来自 FurryBoheCommand） =====
                .then(Commands.literal("test")
                        .then(Commands.literal("affix")
                                .then(Commands.literal("add")
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

                // ===== affix list 快捷命令（列出所有可用词条） =====
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

    // ======================== fursuit_maker_xp 命令实现 ========================

    private static int addFursuitMakerXp(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = EntityArgument.getPlayer(context, "player");
        int xp = IntegerArgumentType.getInteger(context, "xp");
        @Nullable PlayerCapability capability = PlayerCapability.get(player);
        if (capability != null) {
            capability.addFursuitMakerExperience(xp);
            player.sendSystemMessage(Component.literal("XP: %s, Level: %s".formatted(capability.fursuitMakerExperience, capability.fursuitMakerLevel)));
            return 1;
        }
        return 0;
    }

    private static int setFursuitMakerXp(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = EntityArgument.getPlayer(context, "player");
        int xp = IntegerArgumentType.getInteger(context, "xp");
        @Nullable PlayerCapability capability = PlayerCapability.get(player);
        if (capability != null) {
            capability.setFursuitMakerExperience(xp);
            player.sendSystemMessage(Component.literal("XP: %s, Level: %s".formatted(capability.fursuitMakerExperience, capability.fursuitMakerLevel)));
            return 1;
        }
        return 0;
    }

    // ======================== Affix 测试命令实现 ========================

    private static int addAffixToSlot(Player player, String slotName, String affixId) {
        EquipmentSlot slot = parseSlot(slotName);
        if (slot == null) {
            player.sendSystemMessage(Component.literal("§c无效的槽位！可用: head, chest, legs, feet, offhand, mainhand"));
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

        if (cap.hasAffix(affix)) {
            player.sendSystemMessage(Component.literal("§e该物品已有词条: " + affix.getDisplayName()));
            return 0;
        }

        cap.addAffix(affix, 1);
        player.sendSystemMessage(Component.literal("§a已添加词条: §f" + affix.getDisplayName()));
        listAffixesInSlot(player, slotName);
        return 1;
    }

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
                player.sendSystemMessage(Component.literal("§7- " + affix.getDisplayName() + levelStr));
            }
        }
        return 1;
    }

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

    private static int listAllAvailableAffixes(Player player) {
        Map<ResourceLocation, BaseAffix> allAffixes = RegisterAffixs.getAll();

        if (allAffixes.isEmpty()) {
            player.sendSystemMessage(Component.literal("§e暂无可用词条"));
            return 0;
        }

        player.sendSystemMessage(Component.literal("§6=== 所有可用词条 ==="));
        player.sendSystemMessage(Component.literal("§a=== 正面词条 ==="));
        for (Map.Entry<ResourceLocation, BaseAffix> entry : allAffixes.entrySet()) {
            BaseAffix affix = entry.getValue();
            if (affix.getType() == BaseAffix.AffixType.POSITIVE) {
                player.sendSystemMessage(Component.literal("§7- §f" + affix.getId() + " §7(" + affix.getDisplayName() + "§7)"));
            }
        }

        player.sendSystemMessage(Component.literal("§c=== 负面词条 ==="));
        for (Map.Entry<ResourceLocation, BaseAffix> entry : allAffixes.entrySet()) {
            BaseAffix affix = entry.getValue();
            if (affix.getType() == BaseAffix.AffixType.NEGATIVE) {
                player.sendSystemMessage(Component.literal("§7- §f" + affix.getId() + " §7(" + affix.getDisplayName() + "§7)"));
            }
        }

        player.sendSystemMessage(Component.literal("§d=== 特殊词条 ==="));
        for (Map.Entry<ResourceLocation, BaseAffix> entry : allAffixes.entrySet()) {
            BaseAffix affix = entry.getValue();
            if (affix.getType() == BaseAffix.AffixType.SPECIAL) {
                player.sendSystemMessage(Component.literal("§7- §f" + affix.getId() + " §7(" + affix.getDisplayName() + "§7)"));
            }
        }

        return 1;
    }

    // ======================== 工具方法 ========================

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