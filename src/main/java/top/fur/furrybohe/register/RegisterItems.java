package top.fur.furrybohe.register;

import net.minecraft.world.item.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.fur.furrybohe.armor.head.TestFursuitHead;
import top.fur.furrybohe.base.BaseCrystal;
import top.fur.furrybohe.config.repo_configs.ModInfo;
import top.fur.furrybohe.item.*;
import top.fur.furrybohe.item.ItemFurs;
import top.fur.furrybohe.item.crystals.Crystals;
import top.fur.furrybohe.item.crystals.NullCrystal;
import top.fur.furrybohe.item.strings.ItemSewingBox;
import top.fur.furrybohe.item.strings.ItemStringCoil;
import top.fur.furrybohe.item.strings.ItemStrings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static top.fur.furrybohe.register.RegisterBlocks.*;

public class RegisterItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModInfo.MODID);

    public static final Map<String, RegistryObject<Item>> STRING_COIL_MAP = new HashMap<>();
    public static final Map<String, RegistryObject<Item>> FUR_ITEM_MAP = new HashMap<>();
    public static final Map<String, RegistryObject<Item>> STRING_ITEM_MAP = new HashMap<>();
    public static final Map<String, RegistryObject<Item>> CRYSTAL_MAP = new HashMap<>();

    public static List<RegistryObject<Item>> furItemList = new ArrayList<>();
    public static List<RegistryObject<Item>> stringItemList = new ArrayList<>();
    public static List<RegistryObject<Item>> stringCoilList = new ArrayList<>();
    public static List<RegistryObject<Item>> crystalList = new ArrayList<>();

    // 示例：注册一个基础物品
    //public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ITEM_SEWING_BOX = ITEMS.register("sewing_box",()->new ItemSewingBox(new Item.Properties()));
    public static final RegistryObject<Item> ITEM_PIN = ITEMS.register("pin",()->new ItemPin(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ITEM_FOOD_FURRYBOHE = ITEMS.register("furry_bohe",()->new ItemFoodFurryBohe(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_BOTTLE_RESIN = ITEMS.register("resin_bottle",()->new ItemBottleResin(new Item.Properties()));
    public static final RegistryObject<Item> ITEM_RESIN = ITEMS.register("resin",()->new ItemResin(new Item.Properties()));
    public static final RegistryObject<Item> ITEM_HARDEN_RESIN = ITEMS.register("harden_resin",()->new ItemHardenResin(new Item.Properties()));
    public static final RegistryObject<Item> ITEM_CAT_COLLECTOR = ITEMS.register("cat_collector",()->new ItemCatCollector(new Item.Properties()));

    public static final RegistryObject<Item> ITEM_FURRY_WORK_STATION = ITEMS.register("furry_workstation",()->new BlockItem(FURRY_WORK_STATION.get(),new Item.Properties()));
    public static final RegistryObject<Item> ITEM_RESIN_COLLECTOR = ITEMS.register("resin_collector", () -> new BlockItem(RESIN_COLLECTOR.get(), new Item.Properties()));
    public static final RegistryObject<Item> ITEM_CRYSTAL_WORK_BLOCK = ITEMS.register("crystal_block",()->new BlockItem(CRYSTAL_WORK_BLOCK.get(),new Item.Properties()));
    public static final RegistryObject<Item> ITEM_DRYING_RACK = ITEMS.register("drying_rack",()->new BlockItem(DRYING_RACK.get(),new Item.Properties()));

    public static final RegistryObject<Item> ITEM_ARMOR_TEST_FURRY_HEAD = ITEMS.register("test_furry_head",()->new TestFursuitHead(ArmorMaterials.LEATHER, ArmorItem.Type.HELMET,new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    public static void registerItemForeach(){

        for(String color : ItemFurs.FUR_COLORS){
            furItemList.add(ITEMS.register(color,() -> new Item(new Item.Properties().durability(100).setNoRepair())));
            String colorName = color.split("_")[0];
            FUR_ITEM_MAP.put(colorName,furItemList.get(furItemList.size()-1));
        }

        for(String color : ItemStrings.STRINGS_COLORS){
            stringItemList.add(ITEMS.register(color,() -> new Item(new Item.Properties().stacksTo(32))));
            String colorName = color.split("_")[0];
            STRING_ITEM_MAP.put(colorName,stringItemList.get(stringItemList.size()-1));
        }
        for(String color : ItemStringCoil.STRING_COILS_COLORS){
            stringCoilList.add(ITEMS.register(color,() -> new ItemStringCoil(new Item.Properties())));
            String colorName = color.split("_")[0];
            STRING_COIL_MAP.put(colorName, stringCoilList.get(stringCoilList.size()-1));
        }
        Crystals.AddCrystals();
        for (Class<? extends BaseCrystal> crystalClass : Crystals.crystalsList) {
            try {
                var constructor = crystalClass.getConstructor();
                String id = crystalClass.getSimpleName()
                        .replaceAll("Crystal$", "")  // 去掉 Crystal 后缀
                        .toLowerCase() + "_crystal";
                RegistryObject<Item> item = ITEMS.register(id, () -> {
                    try {
                        return constructor.newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to create crystal: " + id, e);
                    }
                });
                CRYSTAL_MAP.put(id, item);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Crystal " + crystalClass.getName() + " must have no-args constructor", e);
            }
        }
    }
    public static void registerItems(){
        registerItemForeach();
    }

    static{
        registerItems();
    }
}
