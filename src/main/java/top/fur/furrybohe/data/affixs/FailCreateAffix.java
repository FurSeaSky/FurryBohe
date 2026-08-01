package top.fur.furrybohe.data.affixs;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
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
    private static final double DROP_CHANCE = 0.01;
    private static final double MOVE_THRESHOLD = 0.0001;

    private final Map<Player, PlayerMovementData> movementCache = new HashMap<>();

    public FailCreateAffix() {
        super("fail_create_affix", AffixRarity.COMMON, AffixType.NEGATIVE, AffixSlot.ANY, 1);
    }

    // ======================== 事件注册 ========================

    @Override
    protected void registerEventHandlers() {
        registerEventHandler(TickEvent.PlayerTickEvent.class, this::onPlayerTick);
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
    private void onPlayerTick(Player player, TickEvent.PlayerTickEvent event) {
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
        return event.phase == TickEvent.Phase.END && !event.player.level().isClientSide;
    }

    private boolean shouldDrop() {
        return RANDOM.nextDouble() < DROP_CHANCE;
    }

    /**
     * 查找带有此词条的护甲
     */
    private ItemStack findAffectedArmor(Player player) {
        ResourceLocation affixId = Furry_bohe.rl(getId());
        for (ItemStack armor : player.getArmorSlots()) {
            if (armor.isEmpty()) continue;
            FurArmorCapability cap = FurArmorCapabilityProvider.get(armor);
            if (cap != null && cap.hasAffix(affixId)) {
                return armor;
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * 执行装备脱落
     */
    private void dropArmor(Player player, ItemStack armor) {
        // 1. 从护甲槽位移除
        for (int i = 0; i < player.getInventory().armor.size(); i++) {
            if (player.getInventory().armor.get(i) == armor) {
                player.getInventory().armor.set(i, ItemStack.EMPTY);
                break;
            }
        }

        // 2. 掉落到地面
        player.drop(armor, false);

        // 3. 清理词条数据
        FurArmorCapability cap = FurArmorCapabilityProvider.get(armor);
        if (cap != null) {
            cap.removeAffix(this);
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