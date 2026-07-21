// ResinCollectorBlockEntity.java - 修正版
package top.fur.furrybohe.blocks.ResinCollector;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import top.fur.furrybohe.register.RegisterBlockEntitys;
import top.fur.furrybohe.register.RegisterItems;

import java.util.Set;

public class ResinCollectorBlockEntity extends BlockEntity {
    private static final int MAX_COOLDOWN = 6000;
    private static final int CHECK_RADIUS = 2;

    private int cooldown = 0;
    private boolean isValidPosition = false;

    public ResinCollectorBlockEntity(BlockPos pos, BlockState state) {
        super(RegisterBlockEntitys.RESIN_COLLECTOR.get(), pos, state);
    }

    // 服务端每tick更新
    public static void serverTick(Level level, BlockPos pos, BlockState state, ResinCollectorBlockEntity entity) {
        if (level.isClientSide) return;

        // 检查位置是否有效（周围是否有云杉/橡木原木）
        boolean wasValid = entity.isValidPosition;
        entity.isValidPosition = entity.isValidPosition(level, pos);

        // 如果位置有效且在有效生物群系中，冷却减少
        if (entity.isValidPosition && entity.cooldown > 0 && isValidBiome(level, pos)) {
            entity.cooldown = Math.max(0, entity.cooldown - 1);

            // 冷却刚完成时发送更新到客户端
            if (entity.cooldown == 0 && wasValid) {
                entity.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }
    }

    // 检查周围是否有云杉或橡木原木
    private boolean isValidPosition(Level level, BlockPos pos) {
        for (int dx = -CHECK_RADIUS; dx <= CHECK_RADIUS; dx++) {
            for (int dz = -CHECK_RADIUS; dz <= CHECK_RADIUS; dz++) {
                BlockPos checkPos = pos.offset(dx, 0, dz);
                BlockState checkState = level.getBlockState(checkPos);

                // 检查是否是云杉原木或橡木原木
                if (checkState.is(net.minecraft.world.level.block.Blocks.OAK_LOG) ||
                        checkState.is(net.minecraft.world.level.block.Blocks.SPRUCE_LOG)) {
                    return true;
                }
            }
        }
        return false;
    }

    // 检查生物群系是否有效 - 使用 BiomeTags
    private static boolean isValidBiome(Level level, BlockPos pos) {
        Holder<Biome> biomeHolder = level.getBiome(pos);
        if (!biomeHolder.is(BiomeTags.IS_OVERWORLD) || biomeHolder.is(BiomeTags.IS_DEEP_OCEAN) || biomeHolder.is(BiomeTags.IS_HILL)) {
            return false;
        }

        Set<Block> logBlocks = Set.of(Blocks.OAK_LOG, Blocks.SPRUCE_LOG);
        Direction[] directions = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN};

        for (Direction dir : directions) {
            if (logBlocks.contains(level.getBlockState(pos.offset(dir.getNormal())).getBlock())) {
                return true;
            }
        }
        return false;
    }

    // 尝试收集树脂
    public boolean tryCollectResin(Player player) {
        if (level == null) return false;

        // 检查是否可以收集
        if (!canCollect()) {
            return false;
        }

        // 获取玩家手中的瓶子
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.getItem() != Items.GLASS_BOTTLE) {
            return false;
        }

        // 消耗瓶子
        if (!player.isCreative()) {
            heldItem.shrink(1);
        }

        // 给玩家一瓶树脂
        ItemStack resinBottle = new ItemStack(RegisterItems.ITEM_BOTTLE_RESIN.get());
        if (!player.getInventory().add(resinBottle)) {
            player.drop(resinBottle, false);
        }

        // 开始冷却
        startCooldown();

        // 发送更新到客户端
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);

        return true;
    }

    // 使用粘液球重置冷却
    public boolean resetCooldownWithSlime(Player player) {
        if (level == null) return false;

        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.getItem() != Items.SLIME_BALL) {
            return false;
        }

        if (cooldown <= 0) {
            return false;
        }

        // 消耗粘液球
        if (!player.isCreative()) {
            heldItem.shrink(1);
        }

        // 重置冷却
        cooldown = 0;
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);

        return true;
    }

    // 是否可以收集
    public boolean canCollect() {
        return cooldown == 0 && isValidPosition && isValidBiome(level, worldPosition);
    }

    // 开始冷却
    public void startCooldown() {
        this.cooldown = MAX_COOLDOWN;
        setChanged();
    }

    // 获取冷却时间（tick）
    public int getCooldown() {
        return cooldown;
    }

    // 获取冷却时间（秒）
    public int getCooldownSeconds() {
        return cooldown / 20;
    }

    // 获取冷却进度 (0.0 ~ 1.0)
    public float getCooldownProgress() {
        return 1.0f - ((float) cooldown / MAX_COOLDOWN);
    }

    // 获取位置是否有效
    public boolean isValidPosition() {
        return isValidPosition;
    }

    // 获取生物群系是否有效
    public boolean isValidBiome() {
        if (level == null) return false;
        return isValidBiome(level, worldPosition);
    }

    // ========== 数据持久化 ==========
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.cooldown = tag.getInt("cooldown");
        this.isValidPosition = tag.getBoolean("isValidPosition");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("cooldown", this.cooldown);
        tag.putBoolean("isValidPosition", this.isValidPosition);
    }

    // ========== 网络同步 ==========
    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("cooldown", this.cooldown);
        tag.putBoolean("isValidPosition", this.isValidPosition);
        return tag;
    }
}