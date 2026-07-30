package top.fur.furrybohe.blocks.CrystalWorkBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import top.fur.furrybohe.register.RegisterBlockEntitys;

public class CrystalWorkBlockEntity extends BlockEntity {
    public CrystalWorkBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(RegisterBlockEntitys.CRYSTAL_WORK_BLOCK_ENTITY.get(),blockPos,blockState);
    }
}
