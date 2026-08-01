package top.fur.furrybohe.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;
import top.fur.furrybohe.Furry_bohe;
import top.fur.furrybohe.base.BaseAffix;
import top.fur.furrybohe.register.RegisterAffixs;

import java.util.*;

@AutoRegisterCapability
public class FurArmorCapability implements INBTSerializable<CompoundTag> {
    // 装备物品Object
    public ItemStack itemStack;
    // 脏污值
    public int dirtyValue = 0;
    // 潮湿度
    public int wetValue = 0;
    // 词条 暂时用Object 之后得上注册表
    public final Map<ResourceLocation,Integer> affixes = new LinkedHashMap<>();
    private final Map<ResourceLocation, CompoundTag> affixData = new HashMap<>();
    private final Map<ResourceLocation, BaseAffix> affixCache = new HashMap<>();
    private final List<BaseAffix> tickListeners = new ArrayList<>();

    public FurArmorCapability(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("dirtyValue", dirtyValue);
        nbt.putInt("wetValue", wetValue);
        CompoundTag affixesNbt = new CompoundTag();
        CompoundTag affixNbt = new CompoundTag();
        for (Map.Entry<ResourceLocation, Integer> entry : affixes.entrySet()) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("level", entry.getValue());
            // 让词条自己序列化额外数据,其实是我懒得写了
            BaseAffix affix = RegisterAffixs.get(entry.getKey());
            if (affix != null) {
                CompoundTag data = affix.serialize();
                if (!data.isEmpty()) {
                    tag.put("data", data);
                }
            }
            CompoundTag storedData = affixData.get(entry.getKey());
            if (storedData != null && !storedData.isEmpty()) {
                tag.put("stored_data", storedData);
            }
            affixNbt.put(entry.getKey().toString(), tag);
        }
        nbt.put("affixes", affixesNbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt == null || nbt.isEmpty()) {
            this.dirtyValue = 0;
            this.wetValue = 0;
            this.affixes.clear();
            return;
        }
        affixes.clear();
        dirtyValue = nbt.getInt("dirtyValue");
        wetValue = nbt.getInt("wetValue");

        affixes.clear();
        affixData.clear();
        affixCache.clear();
        tickListeners.clear();

        // 反序列化词条
        CompoundTag affixNbt = nbt.getCompound("affixes");
        for (String key : affixNbt.getAllKeys()) {
            ResourceLocation affixId = ResourceLocation.tryParse(key);
            if (affixId == null) continue;

            CompoundTag tag = affixNbt.getCompound(key);
            int level = tag.getInt("level");

            try {
                addAffix(affixId, level);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            // 恢复词条数据
            if (tag.contains("stored_data")) {
                affixData.put(affixId, tag.getCompound("stored_data"));
            }

            // 让词条自己反序列化
            BaseAffix affix = RegisterAffixs.get(affixId);
            if (affix != null && tag.contains("data")) {
                affix.deserialize(tag.getCompound("data"));
            }
        }
    }

    public void addAffix(ResourceLocation affixId, int level) throws NoSuchMethodException {
        BaseAffix affix = RegisterAffixs.get(affixId);
        if (affix == null) return;

        affixes.put(affixId, level);
        affixCache.put(affixId, affix);

        // 如果是需要tick的，加入监听列表
        if (affix.getClass().getMethod("onTick", Player.class, ItemStack.class,
                        FurArmorCapability.class, int.class)
                .getDeclaringClass() != BaseAffix.class) {
            tickListeners.add(affix);
        }
    }

    public void removeAffix(ResourceLocation affixId) {
        affixes.remove(affixId);
        affixCache.remove(affixId);
        affixData.remove(affixId);
        tickListeners.remove(RegisterAffixs.get(affixId));
    }
    public void addAffix(BaseAffix affix, int level) {
        try{addAffix(affix.getResourceLocation(), level);} catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasAffix(BaseAffix affix) {
        return hasAffix(affix.getResourceLocation());
    }
    public Map<ResourceLocation, Integer> getAffixes() {
        return Collections.unmodifiableMap(affixes);
    }
    public void clearAffixes() {
        affixes.clear();
    }

    public void removeAffix(BaseAffix affix) {
        removeAffix(Furry_bohe.rl(affix.getId()));
    }

    public int getAffixLevel(ResourceLocation affixId) {
        return affixes.getOrDefault(affixId, 0);
    }

    public boolean hasAffix(ResourceLocation affixId) {
        return affixes.containsKey(affixId);
    }
    public void setAffixData(ResourceLocation affixId, String key, Object value) {
        CompoundTag data = affixData.computeIfAbsent(affixId, k -> new CompoundTag());
        if (value instanceof Integer) {
            data.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            data.putBoolean(key, (Boolean) value);
        } else if (value instanceof String) {
            data.putString(key, (String) value);
        } else if (value instanceof Long) {
            data.putLong(key, (Long) value);
        }
    }

    public <T> T getAffixData(ResourceLocation affixId, String key, T defaultValue) {
        CompoundTag data = affixData.get(affixId);
        if (data == null) return defaultValue;

        if (defaultValue instanceof Integer) {
            return (T) Integer.valueOf(data.getInt(key));
        } else if (defaultValue instanceof Boolean) {
            return (T) Boolean.valueOf(data.getBoolean(key));
        } else if (defaultValue instanceof String) {
            return (T) data.getString(key);
        } else if (defaultValue instanceof Long) {
            return (T) Long.valueOf(data.getLong(key));
        }
        return defaultValue;
    }

    public void removeAffixData(ResourceLocation affixId, String key) {
        CompoundTag data = affixData.get(affixId);
        if (data != null) {
            data.remove(key);
        }
    }

    // ========== Tick 更新 ==========

    public void tickAffixes(Player player) {
        for (BaseAffix affix : tickListeners) {
            int level = getAffixLevel(Furry_bohe.rl(affix.getId()));
            if (level > 0) {
                affix.onTick(player, itemStack, this, level);
            }
        }
    }
}
