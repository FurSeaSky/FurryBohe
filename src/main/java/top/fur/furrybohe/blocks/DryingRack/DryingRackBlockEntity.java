package top.fur.furrybohe.blocks.DryingRack;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import top.fur.furrybohe.register.RegisterBlockEntities;

public class DryingRackBlockEntity extends BlockEntity {
    public @NotNull ItemStack clothesStack = ItemStack.EMPTY;

    public DryingRackBlockEntity(BlockPos pos, BlockState state) {
        super(RegisterBlockEntities.DRYING_RACK_BLOCK_ENTITY.get(), pos,state);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        this.clothesStack = ItemStack.EMPTY;
        if (compoundTag.contains("clothesStack")) {
            this.clothesStack = ItemStack.of(compoundTag.getCompound("clothesStack"));
        }
    }

    @Override
    public void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        compoundTag.put("clothesStack", this.clothesStack.save(new CompoundTag()));
    }
}
