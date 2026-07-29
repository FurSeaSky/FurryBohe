package top.fur.furrybohe.datagen;

import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.fur.furrybohe.config.repo_configs.ModInfo;
import top.fur.furrybohe.datagen.advancements.*;
import top.fur.furrybohe.base.*;
import top.fur.furrybohe.base.BaseAdvancementClass;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = ModInfo.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    // 进度管理器 - 静态内部类
    public static class AdvancementManager implements ForgeAdvancementProvider.AdvancementGenerator {

        private final List<BaseAdvancementClass> advancements = new ArrayList<>();

        public AdvancementManager() {
            registerAdvancements();
        }

        private void registerAdvancements() {
            AllStart allStart = new AllStart();
            Meow meow = new Meow();

            meow.setParent(allStart);

            advancements.add(meow);
            advancements.add(allStart);
        }

        @Override
        public void generate(HolderLookup.Provider registries,
                             Consumer<Advancement> saver,
                             ExistingFileHelper existingFileHelper) {

            // 第一步：初始化所有进度的 Builder
            for (BaseAdvancementClass advancement : advancements) {
                advancement.initBuilder();  // 改为 initBuilder()
            }

            // 第二步：保存所有进度（会自动处理父子关系）
            for (BaseAdvancementClass advancement : advancements) {
                advancement.save(saver, existingFileHelper);
            }
        }
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(
                event.includeServer(),
                (DataProvider.Factory<ForgeAdvancementProvider>) output -> new ForgeAdvancementProvider(
                        output,
                        event.getLookupProvider(),
                        existingFileHelper,
                        List.of(new AdvancementManager())
                )
        );
    }
}