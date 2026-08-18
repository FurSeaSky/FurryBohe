package top.fur.furrybohe.data.affixs;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import top.fur.furrybohe.Furry_bohe;
import top.fur.furrybohe.base.BaseAffix;
import top.fur.furrybohe.capability.FurArmorCapability;
import top.fur.furrybohe.capability.FurArmorCapabilityProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 * 失败制作词条
 * 效果：玩家行动时（移动/跳跃/攻击），有1%概率使装备脱落
 */
public class FailCreateAffix extends BaseAffix {

    private static final Random RANDOM = new Random();
    private static final double DROP_CHANCE = 0.001;
    private static final double MOVE_THRESHOLD = 0.0001;

    private final Map<Player, PlayerMovementData> movementCache = new HashMap<>();

    public FailCreateAffix() {
        super("fail_create", AffixRarity.COMMON, AffixType.NEGATIVE, AffixSlot.ANY, 1);
    }

    // ======================== 事件注册 ========================

    @Override
    public void registerEventHandlers(IEventBus bus) {
        bus.addListener(this::onPlayerTick);
    }

    // ======================== 生命周期 ========================

    @Override
    public void onApply(Player player, ItemStack armor, FurArmorCapability cap, int level) {
        movementCache.put(player, new PlayerMovementData(player.position(), player.onGround()));
    }

    @Override
    public void onRemove(Player player, ItemStack armor, FurArmorCapability cap, int level) {
        movementCache.remove(player);
    }

    // ======================== 核心逻辑 ========================

    /**
     * 每 tick 检测玩家动作，触发脱落判定
     */
    private void onPlayerTick(final TickEvent.PlayerTickEvent event) {
        System.out.println("FailCreateAffix has been actived tick");
        Player player = event.player;
        if (!shouldProcess(event)) return;

        ItemStack armor = findAffectedArmor(player);
        if (armor.isEmpty()) return;

        if (isPlayerActive(player) && shouldDrop()) {
            dropArmor(player, armor);
        }
    }

    /**
     * 检测玩家是否有任何动作
     */
    private boolean isPlayerActive(Player player) {
        PlayerMovementData data = movementCache.get(player);
        if (data == null) {
            movementCache.put(player, new PlayerMovementData(player.position(), player.onGround()));
            return false;
        }

        boolean isMoving = hasMoved(data, player);
        boolean isJumping = hasJumped(data, player);
        boolean isAttacking = player.swinging;

        // 更新缓存
        data.lastPosition = player.position();
        data.wasOnGround = player.onGround();

        return isMoving || isJumping || isAttacking;
    }

    // ======================== 检测方法 ========================

    private boolean hasMoved(PlayerMovementData data, Player player) {
        Vec3 current = player.position();
        Vec3 last = data.lastPosition;
        return current.distanceToSqr(last) > MOVE_THRESHOLD;
    }

    private boolean hasJumped(PlayerMovementData data, Player player) {
        return data.wasOnGround && !player.onGround();
    }

    // ======================== 工具方法 ========================

    private boolean shouldProcess(TickEvent.PlayerTickEvent event) {
        var result = event.phase == TickEvent.Phase.END && !event.player.level().isClientSide;
        System.out.println("isBegin:"+(event.phase == TickEvent.Phase.START)+"isClientSide:"+event.player.level().isClientSide);
        return result;
    }

    private boolean shouldDrop() {
        double rnd = RANDOM.nextDouble()%10;
        System.out.println("Rnd:"+rnd+"Drop:"+DROP_CHANCE+"Drop?:"+(rnd<DROP_CHANCE));
        return  rnd< DROP_CHANCE;
    }

    /**
     * 查找带有此词条的护甲
     */
    private ItemStack findAffectedArmor(Player player) {
        // 记录方法入口
        Furry_bohe.LOGGER.debug("开始查找受影响的盔甲，玩家: {}", player.getName().getString());

        // 获取要查找的附魔ID
        ResourceLocation affixId = Furry_bohe.rl(getId());
        Furry_bohe.LOGGER.debug("目标附魔ID: {}", affixId);

        // 检查玩家是否在线/有效
        if (player == null) {
            Furry_bohe.LOGGER.warn("玩家对象为空，无法查找盔甲");
            return ItemStack.EMPTY;
        }

        // 获取玩家的盔甲栏位并遍历
        Iterable<ItemStack> armorSlots = player.getArmorSlots();
        int slotIndex = 0;

        for (ItemStack armor : armorSlots) {
            Furry_bohe.LOGGER.trace("检查第 {} 个盔甲栏位，物品: {}", slotIndex, armor.isEmpty() ? "空" : armor.getDisplayName().getString());

            if (armor.isEmpty()) {
                Furry_bohe.LOGGER.trace("第 {} 个盔甲栏位为空，跳过", slotIndex);
                slotIndex++;
                continue;
            }

            // 记录物品详情
            Furry_bohe.LOGGER.debug("检查物品: {}, 数量: {}, 物品ID: {}",
                    armor.getDisplayName().getString(),
                    armor.getCount(),
                    armor.getItem().toString());

            try {
                // 获取盔甲能力数据
                FurArmorCapability cap = FurArmorCapabilityProvider.get(armor);

                if (cap == null) {
                    Furry_bohe.LOGGER.debug("物品 {} 没有FurArmor能力数据", armor.getDisplayName().getString());
                    slotIndex++;
                    continue;
                }

                // 检查是否包含目标附魔
                boolean hasAffix = cap.hasAffix(affixId);
// 先记录检查结果
                Furry_bohe.LOGGER.debug("物品 {} 是否包含附魔 {}: {}",
                        armor.getDisplayName().getString(),
                        affixId,
                        hasAffix);

// 输出所有附魔详情
                if (cap.getAffixes() != null && !cap.getAffixes().isEmpty()) {
                    Furry_bohe.LOGGER.debug("物品 {} 当前包含的所有附魔:", armor.getDisplayName().getString());
                    for (ResourceLocation affix : cap.getAffixes().keySet()) {
                        Furry_bohe.LOGGER.debug("  - {}", affix);
                    }
                } else {
                    Furry_bohe.LOGGER.debug("物品 {} 没有任何附魔", armor.getDisplayName().getString());
                }

                if (hasAffix) {
                    Furry_bohe.LOGGER.info("找到受影响的盔甲: {} (槽位: {}), 附魔ID: {}",
                            armor.getDisplayName().getString(),
                            slotIndex,
                            affixId);
                    return armor;
                }

            } catch (Exception e) {
                Furry_bohe.LOGGER.error("检查盔甲 {} 时发生异常: {}",
                        armor.getDisplayName().getString(),
                        e.getMessage(),
                        e);
            }

            slotIndex++;
        }

        // 未找到任何符合条件的盔甲
        Furry_bohe.LOGGER.debug("未在玩家 {} 的盔甲栏位中找到包含附魔 {} 的盔甲",
                player.getName().getString(),
                affixId);
        return ItemStack.EMPTY;
    }

    /**
     * 执行装备脱落
     */
    private void dropArmor(Player player, ItemStack armor) {
        if (armor == null || armor.isEmpty()) return;

        // 遍历所有盔甲槽位
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                ItemStack current = player.getItemBySlot(slot);
                // 找到匹配的物品（比较物品和 NBT）
                if (ItemStack.isSameItemSameTags(current, armor)) {
                    // 把物品从槽位移除并扔到地上
                    player.setItemSlot(slot, ItemStack.EMPTY);
                    player.drop(armor, true);
                    return;
                }
            }
        }
    }

    // ======================== 内部类 ========================

    /**
     * 玩家移动数据缓存
     */
    private static class PlayerMovementData {
        Vec3 lastPosition;
        boolean wasOnGround;

        PlayerMovementData(Vec3 position, boolean onGround) {
            this.lastPosition = position;
            this.wasOnGround = onGround;
        }
    }
}