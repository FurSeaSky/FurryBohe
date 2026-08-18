package top.fur.furrybohe.affixes;

import net.minecraft.resources.ResourceLocation;

public class BaseAffix {
    public final ResourceLocation id;
    public int maxLevel;

    public BaseAffix(ResourceLocation id, int maxLevel) {
        this.id = id;
        this.maxLevel = maxLevel;
    }

    public ResourceLocation getId() {
        return id;
    }

    public int getMaxLevel() {
        return maxLevel;
    }
}
