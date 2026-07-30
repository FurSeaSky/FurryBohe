package top.fur.furrybohe.blocks.FurryWorkStation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import top.fur.furrybohe.register.RegisterBlockEntitys;

public class FurryWorkStationBlockEntity extends BlockEntity {
    public FurryWorkStationBlockEntity(BlockPos pos, BlockState state) {
        super(RegisterBlockEntitys.FURRY_WORK_STATION_ENTITY.get(), pos,state);
    }

}
