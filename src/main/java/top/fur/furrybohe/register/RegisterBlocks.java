package top.fur.furrybohe.register;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.fur.furrybohe.blocks.ResinCollector.ResinCollectorBlock;
import top.fur.furrybohe.config.repo_configs.ModInfo;

public class RegisterBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ModInfo.MODID);
    public static final RegistryObject<Block> RESIN_COLLECTOR = BLOCKS.register("resin_collector", () -> new ResinCollectorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5f).sound(SoundType.WOOD).noOcclusion()));

}
