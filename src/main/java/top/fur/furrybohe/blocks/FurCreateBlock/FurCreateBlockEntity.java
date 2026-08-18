package top.fur.furrybohe.blocks.FurCreateBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import top.fur.furrybohe.register.RegisterBlockEntitys;
import top.fur.furrybohe.register.RegisterBlocks;

public class FurCreateBlockEntity extends BlockEntity {
    public FurCreateBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(RegisterBlockEntitys.FUR_CREATE_BLOCK_ENTITY.get(),blockPos,blockState);
    }
}
