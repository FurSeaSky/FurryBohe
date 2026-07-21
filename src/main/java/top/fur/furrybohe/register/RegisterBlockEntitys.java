package top.fur.furrybohe.register;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.fur.furrybohe.blocks.ResinCollector.ResinCollectorBlockEntity;
import top.fur.furrybohe.config.repo_configs.ModInfo;

public class RegisterBlockEntitys {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ModInfo.MODID);
    public static final RegistryObject<BlockEntityType<ResinCollectorBlockEntity>> RESIN_COLLECTOR = BLOCK_ENTITIES.register("resin_collector", () -> BlockEntityType.Builder.of(ResinCollectorBlockEntity::new, RegisterBlocks.RESIN_COLLECTOR.get()).build(null));
}
