package top.fur.furrybohe.item.crystals;

import net.minecraft.network.chat.Component;
import top.fur.furrybohe.base.BaseCrystal;

public class NullCrystal extends BaseCrystal {
    public NullCrystal() {
        super(new Properties(),CrystalLevel.D,CrystalPart.NONE,CrystalBuff.NEUTRAL,"null_crystal", Component.translatable("text.furrybohe.null_crystal.description.info").toString());
    }
}
