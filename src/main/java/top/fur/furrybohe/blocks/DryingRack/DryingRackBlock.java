package top.fur.furrybohe.blocks.DryingRack;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DryingRackBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 32, 16);
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public DryingRackBlock(Properties properties) {
        super(properties.noOcclusion().noCollission());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        if(blockState.getValue(HALF) != DoubleBlockHalf.LOWER) return null;
        return new DryingRackBlockEntity(blockPos, blockState);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        if(state.getValue(HALF) != DoubleBlockHalf.LOWER) return null;
        return RenderShape.MODEL;
    }
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level,
                               BlockPos pos, CollisionContext context) {
        if(state.getValue(HALF) != DoubleBlockHalf.LOWER) return Block.box(0,-16,0,16,16,16);
        return SHAPE;
    }
    @Override
    public @NotNull InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (blockState.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockPos lowerPos = blockPos.below();
            BlockState lowerState = level.getBlockState(lowerPos);
            if (lowerState.getBlock() == this) {
                return this.use(lowerState, level, lowerPos, player, interactionHand, blockHitResult);
            }
            return InteractionResult.PASS;
        }
        ItemStack handStack = player.getItemInHand(interactionHand);
        BlockEntity be = level.getBlockEntity(blockPos);
        if (!(be instanceof DryingRackBlockEntity dryingRack)) {
            return InteractionResult.PASS;
        }
        ItemStack clothes = dryingRack.clothesStack;
        if (clothes.isEmpty()) {
            if (handStack.getItem() instanceof ArmorItem) {
                dryingRack.clothesStack = handStack.split(1);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        if (handStack.isEmpty()) {
            player.setItemInHand(interactionHand, clothes);
            dryingRack.clothesStack = ItemStack.EMPTY;
            return InteractionResult.SUCCESS;
        }
        if (handStack.getItem() instanceof ArmorItem) {
            if (!player.getInventory().add(clothes.copy())) {
                popResource(level, blockPos, clothes);
            }
            dryingRack.clothesStack = handStack.split(1);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock()) && state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof DryingRackBlockEntity dryingRack) {
                ItemStack stack = dryingRack.clothesStack;
                if (!stack.isEmpty()) {
                    popResource(level, pos, stack);
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();

        if (pos.getY() < level.getMaxBuildHeight() - 1 &&
                level.getBlockState(pos.above()).canBeReplaced(context)) {
            return this.defaultBlockState()
                    .setValue(FACING, context.getHorizontalDirection().getOpposite())
                    .setValue(HALF, DoubleBlockHalf.LOWER);
        }
        return null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING,HALF);
    }

    @Override
    public BlockState rotate(BlockState state, net.minecraft.world.level.block.Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, net.minecraft.world.level.block.Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
    }
    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf half = state.getValue(HALF);
        if (direction.getAxis() == Direction.Axis.Y && ((half == DoubleBlockHalf.LOWER) == (direction == Direction.UP))) {
            if (neighborState.getBlock() != this) return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    // 8. 必须 - 破坏时同步
    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {DoubleBlockHalf half = state.getValue(HALF);BlockPos otherPos = (half == DoubleBlockHalf.LOWER) ? pos.above() : pos.below();BlockState otherState = level.getBlockState(otherPos);
        if (otherState.getBlock() == this) {
            if (!level.isClientSide && !player.isCreative()) {
                level.destroyBlock(otherPos, false);
            } else {
                level.setBlock(otherPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }
}
