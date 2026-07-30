package top.fur.furrybohe.item.crystals;

import net.minecraft.world.item.Item;
import top.fur.furrybohe.base.BaseCrystal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Crystals {
    public static final List<Class<? extends BaseCrystal>> crystalsList = new ArrayList<>();
    public static void AddCrystals(){
            crystalsList.add(NullCrystal.class);
    }
}
