package top.fur.furrybohe.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

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
    public HashMap<Object, Integer> affixes = new LinkedHashMap<>();

    public FurArmorCapability(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("dirtyValue", dirtyValue);
        nbt.putInt("wetValue", wetValue);
        CompoundTag affixesNbt = new CompoundTag();
        // 词条序列化部分 但是现在词条注册表没有实现 留空
        nbt.put("affixes", affixesNbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        affixes.clear();
        dirtyValue = nbt.getInt("dirtyValue");
        wetValue = nbt.getInt("wetValue");
        // 词条反序列化 由于现在词条注册表没有 留空
    }
}
