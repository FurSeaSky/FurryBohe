package top.fur.furrybohe.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import top.fur.furrybohe.capability.PlayerCapability;

public class ModCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("furrybohe")
                        .requires(source -> source.hasPermission(2)) // 需要等级2的权限 (默认为OP)
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
        );
    }

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
}
