package top.fur.furrybohe.blocks.ResinCollector;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.fur.furrybohe.register.RegisterBlockEntitys;

public class ResinCollectorBlock extends BaseEntityBlock {
    public ResinCollectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ResinCollectorBlockEntity(pos,state);
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        super.triggerEvent(state, level, pos, id, param);
        BlockEntity blockentity = level.getBlockEntity(pos);
        return blockentity != null && blockentity.triggerEvent(id, param);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (!level.isClientSide) {
            return createTickerHelper(type, RegisterBlockEntitys.RESIN_COLLECTOR.get(),
                    ResinCollectorBlockEntity::serverTick);
        }
        return null;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS; // 客户端只返回成功
        }

        // 获取 BlockEntity
        if (level.getBlockEntity(pos) instanceof ResinCollectorBlockEntity entity) {
            ItemStack heldItem = player.getItemInHand(hand);

            // 情况1：玩家拿着玻璃瓶 → 尝试收集树脂
            if (heldItem.getItem() == Items.GLASS_BOTTLE) {
                if (entity.tryCollectResin(player)) {
                    // 成功收集，播放音效
                    level.playSound(null, pos, SoundEvents.BOTTLE_FILL,
                            SoundSource.BLOCKS, 1.0F, 1.0F);
                    return InteractionResult.CONSUME;
                } else {
                    // 无法收集，提示玩家
                    player.displayClientMessage(
                            Component.translatable("text.furrybohe.resin_collector_block.cant_collect"),
                            true
                    );
                    return InteractionResult.FAIL;
                }
            }

            // 情况2：玩家拿着粘液球 → 重置冷却
            if (heldItem.getItem() == Items.SLIME_BALL) {
                if (entity.resetCooldownWithSlime(player)) {
                    // 成功重置，播放音效
                    level.playSound(null, pos, SoundEvents.SLIME_BLOCK_PLACE,
                            SoundSource.BLOCKS, 1.0F, 1.0F);
                    player.displayClientMessage(
                            Component.translatable("text.furrybohe.resin_collector_block.resetCD"),
                            true
                    );
                    return InteractionResult.CONSUME;
                } else {
                    player.displayClientMessage(
                            Component.translatable("text.furrybohe.resin_collector_block.not_need_resetCD"),
                            true
                    );
                    return InteractionResult.FAIL;
                }
            }

            // 情况3：手持其他物品，显示状态
            if (player.isShiftKeyDown()) { // 潜行时显示信息
                int seconds = entity.getCooldownSeconds();
                boolean canCollect = entity.canCollect();
                player.displayClientMessage(
                        Component.translatable("text.furrybohe.resin_collector_block.info", Boolean.toString(canCollect),seconds),
                        true
                );
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}
