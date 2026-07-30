package top.fur.furrybohe.command.tests;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class AlwaysTPCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("alwaystp")
                        .requires(source -> source.hasPermission(2)) // 需要OP权限
                        .then(Commands.argument("x", DoubleArgumentType.doubleArg())
                                .then(Commands.argument("y", DoubleArgumentType.doubleArg())
                                        .then(Commands.argument("z", DoubleArgumentType.doubleArg())
                                                .executes(AlwaysTPCommand::execute)
                                        )
                                )
                        )
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        double x = DoubleArgumentType.getDouble(context, "x");
        double y = DoubleArgumentType.getDouble(context, "y");
        double z = DoubleArgumentType.getDouble(context, "z");

        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayer();

        if (player == null) {
            source.sendFailure(Component.literal("只有玩家可以使用此命令"));
            return 0;
        }
        player.connection.teleport(x, y, z, player.getYRot(), player.getXRot());

        source.sendSuccess(
                ()->Component.literal(
                        String.format("已传送到 (%.1f, %.1f, %.1f)", x, y, z)
                ),
                true
        );

        return 1;
    }
}