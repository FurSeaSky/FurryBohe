package top.fur.furrybohe.blocks.FurryWorkStation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import top.fur.furrybohe.register.RegisterBlockEntities;

public class FurryWorkStationBlockEntity extends BlockEntity {
    public FurryWorkStationBlockEntity(BlockPos pos, BlockState state) {
        super(RegisterBlockEntities.FURRY_WORK_STATION_ENTITY.get(), pos,state);
    }

}
