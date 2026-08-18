package top.fur.furrybohe.register;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.fur.furrybohe.blocks.CrystalWorkBlock.CrystalWorkBlock;
import top.fur.furrybohe.blocks.DryingRack.DryingRackBlock;
import top.fur.furrybohe.blocks.FurCreateBlock.FurCreateBlock;
import top.fur.furrybohe.blocks.FurryWorkStation.FurryWorkStationBlock;
import top.fur.furrybohe.blocks.ResinCollector.ResinCollectorBlock;
import top.fur.furrybohe.config.repo_configs.ModInfo;

public class RegisterBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ModInfo.MODID);
    public static final RegistryObject<Block> RESIN_COLLECTOR = BLOCKS.register("resin_collector", () -> new ResinCollectorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5f).sound(SoundType.GILDED_BLACKSTONE).noOcclusion()));
    public static final RegistryObject<Block> FURRY_WORK_STATION = BLOCKS.register("furry_workstation",()->new FurryWorkStationBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5f).sound(SoundType.BONE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> CRYSTAL_WORK_BLOCK = BLOCKS.register("crystal_work_block",()->new CrystalWorkBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5f).sound(SoundType.GILDED_BLACKSTONE).noOcclusion()));
    public static final RegistryObject<Block> DRYING_RACK = BLOCKS.register("drying_rack",()->new DryingRackBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5f).sound(SoundType.WOOD).noOcclusion()));
    public static final RegistryObject<Block> FUR_CREATE_BLOCK = BLOCKS.register("fur_create_block",()->new FurCreateBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5f).sound(SoundType.ANVIL)));
}
