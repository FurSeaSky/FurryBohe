package top.fur.furrybohe.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import top.fur.furrybohe.Furry_bohe;
import top.fur.furrybohe.capability.FurArmorCapability;
import top.fur.furrybohe.register.RegisterEventHandlers;

import java.util.function.BiConsumer;

public abstract class BaseAffix {
    private final RegisterEventHandlers eventHandlers = new RegisterEventHandlers();
    private final String id;
    private final AffixRarity rarity;
    private final AffixType type;
    private final AffixSlot slot; // 可装备的部位
    private final int maxLevel;

    public BaseAffix(String id, AffixRarity rarity, AffixType type, AffixSlot slot, int maxLevel) {
        this.id = id;
        this.rarity = rarity;
        this.type = type;
        this.slot = slot;
        this.maxLevel = maxLevel;
    }
    /**
     * 应用词条效果（装备时调用）
     */
    public abstract void onApply(Player player, ItemStack armor, FurArmorCapability cap, int level);

    /**
     * 移除词条效果（卸下时调用）
     */
    public abstract void onRemove(Player player, ItemStack armor, FurArmorCapability cap, int level);

    /**
     * 每tick更新（需要持续效果的词条实现）
     */
    public void onTick(Player player, ItemStack armor, FurArmorCapability cap, int level) {}

    /**
     * 序列化词条特有数据
     */
    public CompoundTag serialize() {return new CompoundTag();}

    /**
     * 反序列化词条特有数据
     */
    public void deserialize(CompoundTag nbt) {}

    public void registerEventHandlers(IEventBus bus) {}

    public String getId() { return id; }
    public AffixRarity getRarity() { return rarity; }
    public AffixType getType() { return type; }
    public AffixSlot getSlot() { return slot; }
    public int getMaxLevel() { return maxLevel; }
    public RegisterEventHandlers getEventHandlers() { return eventHandlers; }

    public boolean isCompatibleWith(ItemStack stack) {
        return slot.matches(stack);
    }
    public enum AffixRarity {
        COMMON, UNCOMMON, RARE, EPIC, LEGENDARY, SPECIAL
    }

    public enum AffixType {
        POSITIVE,    // 正面
        NEGATIVE,    // 负面
        SPECIAL      // 特殊
    }

    public enum AffixSlot {
        HEAD, BODY, LEGS, FEET, TAIL, ANY;

        public boolean matches(ItemStack stack) {
            return true;
        }
    }
    public Component getDisplayName() {
        return Component.translatable("affix." + id + ".name");
    }
    public Component getDescription() {
        return Component.translatable("affix." + id + ".desc");
    }

    public ResourceLocation getResourceLocation() {
        return Furry_bohe.rl(id);
    }
}