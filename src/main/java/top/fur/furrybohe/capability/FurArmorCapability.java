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
    public final Map<ResourceLocation,Integer> affixesLevel = new LinkedHashMap<>();
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

        CompoundTag affixNbt = new CompoundTag();  // ← 直接用这个
        for (Map.Entry<ResourceLocation, Integer> entry : affixesLevel.entrySet()) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("level", entry.getValue());

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

            affixNbt.put(entry.getKey().toString(), tag);  // ← 存到 affixNbt
        }

        nbt.put("affixesLevel", affixNbt);  // ← 存 affixNbt，不是空对象！
        return nbt;
    }
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt == null || nbt.isEmpty()) {
            this.dirtyValue = 0;
            this.wetValue = 0;
            this.affixesLevel.clear();
            this.affixCache.clear();
            this.tickListeners.clear();
            return;
        }

        // 清空所有数据
        affixesLevel.clear();
        affixCache.clear();
        tickListeners.clear();
        affixData.clear();

        dirtyValue = nbt.getInt("dirtyValue");
        wetValue = nbt.getInt("wetValue");

        CompoundTag affixNbt = nbt.getCompound("affixesLevel");
        if (affixNbt.isEmpty()) {
            return;  // ← 没有数据，直接返回
        }

        for (String key : affixNbt.getAllKeys()) {
            ResourceLocation affixId = ResourceLocation.tryParse(key);
            if (affixId == null) continue;

            CompoundTag tag = affixNbt.getCompound(key);
            int level = tag.getInt("level");

            // 直接操作 Map，不调用 addAffix（避免 tickListeners 重复添加）
            BaseAffix affix = RegisterAffixs.get(affixId);
            if (affix == null) {
                Furry_bohe.LOGGER.warn("未知词条: {}", affixId);
                continue;
            }

            affixesLevel.put(affixId, level);
            affixCache.put(affixId, affix);

            // 恢复词条数据
            if (tag.contains("stored_data")) {
                affixData.put(affixId, tag.getCompound("stored_data"));
            }

            // 让词条自己反序列化
            if (tag.contains("data")) {
                affix.deserialize(tag.getCompound("data"));
            }

            // 检查是否需要 tick
            try {
                if (affix.getClass().getMethod("onTick", Player.class, ItemStack.class,
                                FurArmorCapability.class, int.class)
                        .getDeclaringClass() != BaseAffix.class) {
                    tickListeners.add(affix);
                }
            } catch (NoSuchMethodException e) {
                // 没有 onTick 方法，忽略
            }
        }
    }

    public void addAffix(ResourceLocation affixId, int level) {
        BaseAffix affix = RegisterAffixs.get(affixId);
        if (affix == null) return;

        affixesLevel.put(affixId, level);
        affixCache.put(affixId, affix);

        // 检查是否需要 tick
        try {
            if (affix.getClass().getMethod("onTick", Player.class, ItemStack.class,
                            FurArmorCapability.class, int.class)
                    .getDeclaringClass() != BaseAffix.class) {
                if (!tickListeners.contains(affix)) {  // ← 避免重复添加
                    tickListeners.add(affix);
                }
            }
        } catch (NoSuchMethodException e) {
            // 没有 onTick 方法，忽略
        }
    }
    public void removeAffix(ResourceLocation affixId) {
        affixesLevel.remove(affixId);
        affixCache.remove(affixId);
        affixData.remove(affixId);
        tickListeners.remove(RegisterAffixs.get(affixId));
    }
    public void addAffix(BaseAffix affix, int level) {
        addAffix(affix.getResourceLocation(), level);
    }

    public boolean hasAffix(BaseAffix affix) {
        return hasAffix(affix.getResourceLocation());
    }
    public Map<ResourceLocation, Integer> getAffixes() {
        return Collections.unmodifiableMap(affixesLevel);
    }
    public void clearAffixes() {
        affixesLevel.clear();
    }

    public void removeAffix(BaseAffix affix) {
        removeAffix(Furry_bohe.rl(affix.getId()));
    }

    public int getAffixLevel(ResourceLocation affixId) {
        return affixesLevel.getOrDefault(affixId, 0);
    }

    public boolean hasAffix(ResourceLocation affixId) {
        return affixCache.containsKey(affixId);
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
