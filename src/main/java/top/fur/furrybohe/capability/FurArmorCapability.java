package top.fur.furrybohe.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.fur.furrybohe.Furry_bohe;
import top.fur.furrybohe.base.BaseAffix;
import top.fur.furrybohe.register.RegisterAffixs;

import java.util.*;

@AutoRegisterCapability
public class FurArmorCapability implements INBTSerializable<CompoundTag> {

    // ========== 基础数据 ==========
    public ItemStack itemStack;
    public int dirtyValue = 0;
    public int wetValue = 0;

    // ========== 两套词条系统（保持同步） ==========
    // 你的系统：用 ResourceLocation 做 Key
    public final Map<ResourceLocation, Integer> affixesLevel = new LinkedHashMap<>();
    private final Map<ResourceLocation, CompoundTag> affixData = new HashMap<>();
    private final Map<ResourceLocation, BaseAffix> affixCache = new HashMap<>();
    private final List<BaseAffix> tickListeners = new ArrayList<>();

    // 他的系统：用 BaseAffix 做 Key
    public final Map<BaseAffix, Integer> affixes = new LinkedHashMap<>();

    // ========== 构造 ==========
    public FurArmorCapability(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    // ========== 静态工具 ==========
    public static @Nullable FurArmorCapability get(@Nullable ItemStack stack) {
        return stack == null ? null : stack.getCapability(FurArmorCapabilityProvider.capability).orElse(null);
    }

    public static @NotNull LazyOptional<FurArmorCapability> getOptional(@Nullable ItemStack stack) {
        return stack == null ? LazyOptional.empty() : stack.getCapability(FurArmorCapabilityProvider.capability);
    }

    public static int getAffixLevel(@Nullable ItemStack stack, @Nullable BaseAffix affix) {
        if (stack == null || affix == null) return 0;
        FurArmorCapability cap = get(stack);
        return cap == null ? 0 : cap.getAffixLevel(affix);
    }

    // ========== 同步核心方法 ==========
    private void syncToAffixes(ResourceLocation id, int level) {
        BaseAffix affix = RegisterAffixs.get(id);
        if (affix != null) {
            if (level > 0) {
                affixes.put(affix, level);
            } else {
                affixes.remove(affix);
            }
        }
    }

    private void syncToAffixesLevel(BaseAffix affix, int level) {
        ResourceLocation id = affix.getResourceLocation();
        if (level > 0) {
            affixesLevel.put(id, level);
            affixCache.put(id, affix);
        } else {
            affixesLevel.remove(id);
            affixCache.remove(id);
        }
    }

    private void syncRemoveAffix(ResourceLocation id) {
        BaseAffix affix = RegisterAffixs.get(id);
        affixesLevel.remove(id);
        affixCache.remove(id);
        affixData.remove(id);
        if (affix != null) {
            affixes.remove(affix);
            tickListeners.remove(affix);
        }
    }

    // ========== 添加词条（你的方式） ==========
    public void addAffix(ResourceLocation affixId, int level) {
        BaseAffix affix = RegisterAffixs.get(affixId);
        if (affix == null) return;
        if (level <= 0) {
            removeAffix(affixId);
            return;
        }

        affixesLevel.put(affixId, level);
        affixCache.put(affixId, affix);
        affixes.put(affix, level);  // ← 同步到他的系统

        // tick 监听
        try {
            if (affix.getClass().getMethod("onTick", Player.class, ItemStack.class,
                            FurArmorCapability.class, int.class)
                    .getDeclaringClass() != BaseAffix.class) {
                if (!tickListeners.contains(affix)) {
                    tickListeners.add(affix);
                }
            }
        } catch (NoSuchMethodException ignored) {}
    }

    public void addAffix(BaseAffix affix, int level) {
        addAffix(affix.getResourceLocation(), level);
    }

    // ========== 添加词条（他的方式，带等级上限） ==========
    public void setAffixLevel(@NotNull BaseAffix affix, int level) {
        int maxLevel = affix.getMaxLevel();
        level = Math.max(0, Math.min(level, maxLevel));
        if (level == 0) {
            removeAffix(affix);
            return;
        }
        addAffix(affix, level);  // 复用你的方法，自动同步
    }

    public void setAffixLevel(@Nullable ItemStack stack, @NotNull BaseAffix affix, int level) {
        FurArmorCapability cap = get(stack);
        if (cap == null) return;
        cap.setAffixLevel(affix, level);
    }

    // 不检查等级上限
    public void __setAffixLevel(@NotNull BaseAffix affix, int level) {
        addAffix(affix, level);
    }

    // ========== 查询 ==========
    public int getAffixLevel(ResourceLocation affixId) {
        return affixesLevel.getOrDefault(affixId, 0);
    }

    public int getAffixLevel(BaseAffix affix) {
        return affixes.getOrDefault(affix, 0);
    }

    public boolean hasAffix(ResourceLocation affixId) {
        return affixCache.containsKey(affixId);
    }

    public boolean hasAffix(BaseAffix affix) {
        return hasAffix(affix.getResourceLocation());
    }

    public Map<ResourceLocation, Integer> getAffixes() {
        return Collections.unmodifiableMap(affixesLevel);
    }

    // ========== 移除 ==========
    public void removeAffix(ResourceLocation affixId) {
        syncRemoveAffix(affixId);
    }

    public void removeAffix(BaseAffix affix) {
        removeAffix(affix.getResourceLocation());
    }

    public void clearAffixes() {
        affixesLevel.clear();
        affixCache.clear();
        affixData.clear();
        tickListeners.clear();
        affixes.clear();
    }

    // ========== 额外数据 ==========
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
        if (data != null) data.remove(key);
    }

    // ========== Tick ==========
    public void tickAffixes(Player player) {
        for (BaseAffix affix : tickListeners) {
            int level = getAffixLevel(affix.getResourceLocation());
            if (level > 0) {
                affix.onTick(player, itemStack, this, level);
            }
        }
    }

    // ========== NBT 序列化 ==========
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("dirtyValue", dirtyValue);
        nbt.putInt("wetValue", wetValue);

        // 你的系统：affixesLevel
        CompoundTag affixNbt = new CompoundTag();
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

            affixNbt.put(entry.getKey().toString(), tag);
        }
        nbt.put("affixesLevel", affixNbt);

        // 他的系统：affixes（兼容旧存档）
        CompoundTag affixesNbt = new CompoundTag();
        for (Map.Entry<BaseAffix, Integer> entry : affixes.entrySet()) {
            affixesNbt.putInt(entry.getKey().getId().toString(), entry.getValue());
        }
        nbt.put("affixes", affixesNbt);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        // 清空所有
        affixesLevel.clear();
        affixCache.clear();
        tickListeners.clear();
        affixData.clear();
        affixes.clear();

        if (nbt == null || nbt.isEmpty()) {
            this.dirtyValue = 0;
            this.wetValue = 0;
            return;
        }

        dirtyValue = nbt.getInt("dirtyValue");
        wetValue = nbt.getInt("wetValue");

        // 读取他的系统数据（优先，因为新代码可能用这个写）
        if (nbt.contains("affixes")) {
            CompoundTag affixesNbt = nbt.getCompound("affixes");
            for (String key : affixesNbt.getAllKeys()) {
                ResourceLocation id = ResourceLocation.tryParse(key);
                if (id == null) continue;
                BaseAffix affix = RegisterAffixs.get(id);
                if (affix == null) continue;
                int level = affixesNbt.getInt(key);
                affixes.put(affix, level);
                // 同步到你的系统
                affixesLevel.put(id, level);
                affixCache.put(id, affix);
                // tick 监听
                try {
                    if (affix.getClass().getMethod("onTick", Player.class, ItemStack.class,
                                    FurArmorCapability.class, int.class)
                            .getDeclaringClass() != BaseAffix.class) {
                        if (!tickListeners.contains(affix)) {
                            tickListeners.add(affix);
                        }
                    }
                } catch (NoSuchMethodException ignored) {}
            }
        }

        // 读取你的系统数据（如果 affixes 没有读到，用这个）
        CompoundTag affixNbt = nbt.getCompound("affixesLevel");
        if (affixNbt != null && !affixNbt.isEmpty()) {
            for (String key : affixNbt.getAllKeys()) {
                ResourceLocation affixId = ResourceLocation.tryParse(key);
                if (affixId == null) continue;
                // 如果已经通过 affixes 加载过了，就跳过
                if (affixesLevel.containsKey(affixId)) continue;

                CompoundTag tag = affixNbt.getCompound(key);
                int level = tag.getInt("level");

                BaseAffix affix = RegisterAffixs.get(affixId);
                if (affix == null) {
                    Furry_bohe.LOGGER.warn("未知词条: {}", affixId);
                    continue;
                }

                affixesLevel.put(affixId, level);
                affixCache.put(affixId, affix);
                affixes.put(affix, level);

                if (tag.contains("stored_data")) {
                    affixData.put(affixId, tag.getCompound("stored_data"));
                }
                if (tag.contains("data")) {
                    affix.deserialize(tag.getCompound("data"));
                }

                try {
                    if (affix.getClass().getMethod("onTick", Player.class, ItemStack.class,
                                    FurArmorCapability.class, int.class)
                            .getDeclaringClass() != BaseAffix.class) {
                        if (!tickListeners.contains(affix)) {
                            tickListeners.add(affix);
                        }
                    }
                } catch (NoSuchMethodException ignored) {}
            }
        }
    }
}