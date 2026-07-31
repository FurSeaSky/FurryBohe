package top.fur.furrybohe.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.fur.furrybohe.affixes.BaseAffix;
import top.fur.furrybohe.register.RegisterAffixes;

import java.util.HashMap;
import java.util.LinkedHashMap;

@AutoRegisterCapability
public class FurArmorCapability implements INBTSerializable<CompoundTag> {
    // 装备物品Object
    public ItemStack itemStack;
    // 脏污值
    public int dirtyValue = 0;
    // 潮湿度
    public int wetValue = 0;
    // 词条 暂时用Object 之后得上注册表
    public HashMap<BaseAffix, Integer> affixes = new LinkedHashMap<>();

    public FurArmorCapability(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public static @Nullable FurArmorCapability get(@Nullable ItemStack stack) {
        return stack == null ? null : stack.getCapability(FurArmorCapabilityProvider.capability).orElse(null);
    }

    public static @NotNull LazyOptional<FurArmorCapability> getOptional(@Nullable ItemStack stack) {
        return stack == null ? LazyOptional.empty() : stack.getCapability(FurArmorCapabilityProvider.capability);
    }

    public int getAffixLevel(@Nullable BaseAffix affix) {
        return affixes.getOrDefault(affix, 0);
    }

    public static int getAffixLevel(@Nullable ItemStack stack, @Nullable BaseAffix affix) {
        if (stack == null || affix == null) {
            return 0;
        }
        FurArmorCapability cap = get(stack);
        if (cap == null) {
            return 0;
        }
        return cap.getAffixLevel(affix);
    }

    // 会检查等级上限
    public void setAffixLevel(@NotNull BaseAffix affix, int level) {
        int maxLevel = affix.getMaxLevel();
        level = Math.max(0, Math.min(level, maxLevel));
        if (level == 0) {
            return;
        }
        affixes.put(affix, level);
    }

    public void setAffixLevel(@Nullable ItemStack stack, @NotNull BaseAffix affix, int level) {
        FurArmorCapability cap = get(stack);
        if (cap == null) {
            return;
        }
        cap.setAffixLevel(affix, level);
    }

    // 不检查等级上限 用的较少(应该是超出等级的有概率不生效) 用__做个前缀
    public void __setAffixLevel(@NotNull BaseAffix affix, int level) {
        affixes.put(affix, level);
    }

    // __setAffixLevel用的太少 不提供静态函数

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("dirtyValue", dirtyValue);
        nbt.putInt("wetValue", wetValue);
        CompoundTag affixesNbt = new CompoundTag();
        for (BaseAffix affix : affixes.keySet()) {
            affixesNbt.putInt(affix.getId().toString(), affixes.get(affix));
        }
        nbt.put("affixes", affixesNbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        affixes.clear();
        dirtyValue = 0;
        wetValue = 0;
        if (nbt.contains("dirtyValue")) {
            dirtyValue = nbt.getInt("dirtyValue");
        }
        if (nbt.contains("wetValue")) {
            wetValue = nbt.getInt("wetValue");
        }
        if (nbt.contains("affixes")) {
            CompoundTag affixesNbt = nbt.getCompound("affixes");
            for (String key : affixesNbt.getAllKeys()) {
                ResourceLocation id = ResourceLocation.parse(key);
                BaseAffix affix = RegisterAffixes.AFFIXES.get(id);
                if (affix != null) {
                    affixes.put(affix, affixesNbt.getInt(key));
                }
            }
        }
    }
}
