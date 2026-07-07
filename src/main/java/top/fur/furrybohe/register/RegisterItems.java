package top.fur.furrybohe.register;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.fur.furrybohe.config.repo_configs.ModInfo;
import top.fur.furrybohe.item.furs.ItemFurs;
import top.fur.furrybohe.item.strings.ItemStringCoil;
import top.fur.furrybohe.item.strings.ItemStrings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RegisterItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModInfo.MODID);

    // 示例：注册一个基础物品
    //public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () -> new Item(new Item.Properties()));

    public static void registerItemForeach(){

        List<RegistryObject<Item>> furItemList = new ArrayList<>();
        for(String color : ItemFurs.FUR_COLORS){
            furItemList.add(ITEMS.register(color,() -> new Item(new Item.Properties().durability(100).setNoRepair())));
        }

        List<RegistryObject<Item>> stringItemList = new ArrayList<>();
        for(String color : ItemStrings.STRINGS_COLORS){
            stringItemList.add(ITEMS.register(color,() -> new Item(new Item.Properties().stacksTo(32))));
        }
        List<RegistryObject<Item>> StringCoilList = new ArrayList<>();
        for(String color : ItemStringCoil.STRING_COILS_COLORS){
            StringCoilList.add(ITEMS.register(color,() -> new ItemStringCoil(new Item.Properties())));
        }
    }
    public static void registerItems(){
        registerItemForeach();
    }

    static{
        registerItems();
    }
}
