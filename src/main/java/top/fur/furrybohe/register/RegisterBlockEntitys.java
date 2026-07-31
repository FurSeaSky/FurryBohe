package top.fur.furrybohe.register;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.fur.furrybohe.blocks.CrystalWorkBlock.CrystalWorkBlockEntity;
import top.fur.furrybohe.blocks.FurCreateBlock.FurCreateBlock;
import top.fur.furrybohe.blocks.FurCreateBlock.FurCreateBlockEntity;
import top.fur.furrybohe.blocks.FurryWorkStation.FurryWorkStationBlockEntity;
import top.fur.furrybohe.blocks.ResinCollector.ResinCollectorBlockEntity;
import top.fur.furrybohe.config.repo_configs.ModInfo;

public class RegisterBlockEntitys {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ModInfo.MODID);
    public static final RegistryObject<BlockEntityType<ResinCollectorBlockEntity>> RESIN_COLLECTOR_ENTITY = BLOCK_ENTITIES.register("resin_collector", () -> BlockEntityType.Builder.of(ResinCollectorBlockEntity::new, RegisterBlocks.RESIN_COLLECTOR.get()).build(null));
    public static final RegistryObject<BlockEntityType<FurryWorkStationBlockEntity>> FURRY_WORK_STATION_ENTITY = BLOCK_ENTITIES.register("furry_workstation",()->BlockEntityType.Builder.of(FurryWorkStationBlockEntity::new,RegisterBlocks.FURRY_WORK_STATION.get()).build(null));
    public static final RegistryObject<BlockEntityType<CrystalWorkBlockEntity>> CRYSTAL_WORK_BLOCK_ENTITY = BLOCK_ENTITIES.register("crystal_work_block",()->BlockEntityType.Builder.of(CrystalWorkBlockEntity::new,RegisterBlocks.CRYSTAL_WORK_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<FurCreateBlockEntity>>  FUR_CREATE_BLOCK_ENTITY = BLOCK_ENTITIES.register("fur_create_block",()->BlockEntityType.Builder.of(FurCreateBlockEntity::new,RegisterBlocks.FUR_CREATE_BLOCK.get()).build(null));
}
