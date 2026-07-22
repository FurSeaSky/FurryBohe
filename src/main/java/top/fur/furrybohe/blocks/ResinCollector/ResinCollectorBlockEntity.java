package top.fur.furrybohe.blocks.ResinCollector;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import top.fur.furrybohe.register.RegisterBlockEntitys;
import top.fur.furrybohe.register.RegisterItems;

public class ResinCollectorBlockEntity extends BlockEntity {
    private static final Logger LOGGER = LogManager.getLogger(ResinCollectorBlockEntity.class);
    private static final int MAX_COOLDOWN = 6000;
    private static final int CHECK_RADIUS = 2;
    private static final int VALIDATE_INTERVAL = 20;

    private int cooldown = MAX_COOLDOWN;
    private boolean isValidPosition = false;
    private int validateCounter = 0;

    public ResinCollectorBlockEntity(BlockPos pos, BlockState state) {
        super(RegisterBlockEntitys.RESIN_COLLECTOR.get(), pos, state);
        LOGGER.info("[FURBOHE] ResinCollectorBlockEntity created at position: {}", pos);
    }

    private boolean validateLocation() {
        if (level == null) {
            LOGGER.warn("[FURBOHE] validateLocation called with null level at position: {}", worldPosition);
            return false;
        }

        // LOGGER.debug("[FURBOHE] Starting location validation at position: {}", worldPosition);

        Holder<Biome> biomeHolder = level.getBiome(worldPosition);
        boolean isDeepOcean = biomeHolder.is(BiomeTags.IS_DEEP_OCEAN);
        boolean isHill = biomeHolder.is(BiomeTags.IS_HILL);
        boolean isOverworld = biomeHolder.is(BiomeTags.IS_OVERWORLD);

        // LOGGER.debug("[FURBOHE] Biome check at {} - DeepOcean: {}, Hill: {}, Overworld: {}",
        //        worldPosition, isDeepOcean, isHill, isOverworld);

        if (isDeepOcean || isHill || !isOverworld) {
            // LOGGER.debug("[FURBOHE] Biome validation failed at {} - DeepOcean: {}, Hill: {}, Overworld: {}",
            // worldPosition, isDeepOcean, isHill, isOverworld);
            return false;
        }

        for (int dx = -CHECK_RADIUS; dx <= CHECK_RADIUS; dx++) {
            for (int dy = -CHECK_RADIUS; dy <= CHECK_RADIUS; dy++) {
                for (int dz = -CHECK_RADIUS; dz <= CHECK_RADIUS; dz++) {
                    BlockPos checkPos = worldPosition.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(checkPos);
                    if (state.is(Blocks.OAK_LOG) || state.is(Blocks.SPRUCE_LOG)) {
                        // LOGGER.debug("[FURBOHE] Found valid log at offset ({}, {}, {}) at position: {}",
                        //        dx, dy, dz, checkPos);
                        return true;
                    }
                }
            }
        }

        // LOGGER.debug("[FURBOHE] No valid logs found within radius {} at position: {}", CHECK_RADIUS, worldPosition);
        return false;
    }

    private boolean validateAndUpdate() {
        // LOGGER.debug("[FURBOHE] validateAndUpdate called at position: {}", worldPosition);
        boolean previousValid = this.isValidPosition;
        this.isValidPosition = validateLocation();
        // LOGGER.debug("[FURBOHE] validateAndUpdate result at {} - previous: {}, current: {}",
        //        worldPosition, previousValid, this.isValidPosition);
        return this.isValidPosition;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ResinCollectorBlockEntity entity) {
        if (level.isClientSide) return;

        LOGGER.trace("[FURBOHE] serverTick called at position: {}, cooldown: {}, isValid: {}",
                pos, entity.cooldown, entity.isValidPosition);

        boolean wasValid = entity.isValidPosition;
        int previousCooldown = entity.cooldown;

        entity.validateCounter++;
        if (entity.validateCounter >= VALIDATE_INTERVAL) {
            // LOGGER.debug("[FURBOHE] Validation interval reached at position: {}, performing validation", pos);
            entity.validateCounter = 0;
            entity.isValidPosition = entity.validateLocation();
            // LOGGER.debug("[FURBOHE] Validation result at {} after interval: {}", pos, entity.isValidPosition);
        }

        if (entity.isValidPosition && entity.cooldown > 0) {
            entity.cooldown = Math.max(0, entity.cooldown - 1);
            // LOGGER.debug("[FURBOHE] Cooldown decreased at {} from {} to {}",
            //        pos, previousCooldown, entity.cooldown);

            if (entity.cooldown == 0) {
                LOGGER.info("[FURBOHE] Cooldown completed at position: {}, sending update to client", pos);
                entity.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
            }
        } else if (!entity.isValidPosition && entity.cooldown > 0) {
            // LOGGER.debug("[FURBOHE] Position invalid at {}, cooldown paused at: {}", pos, entity.cooldown);
        } else if (entity.isValidPosition && entity.cooldown == 0) {
            LOGGER.trace("[FURBOHE] Position valid and cooldown ready at {}", pos);
        }

        if (entity.cooldown == 0 && !wasValid && entity.isValidPosition) {
            LOGGER.info("[FURBOHE] Position became valid and cooldown ready at {}, sending update", pos);
            entity.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }

    public boolean tryCollectResin(Player player) {
        if (level == null) {
            LOGGER.warn("[FURBOHE] tryCollectResin called with null level at position: {}", worldPosition);
            return false;
        }

        LOGGER.info("[FURBOHE] Player {} attempting to collect resin at position: {}",
                player.getName().getString(), worldPosition);

        if (!canCollect()) {
            // LOGGER.debug("[FURBOHE] Collection failed for player {} - canCollect returned false at position: {}",
            //        player.getName().getString(), worldPosition);
            return false;
        }

        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.getItem() != Items.GLASS_BOTTLE) {
            // LOGGER.debug("[FURBOHE] Player {} not holding glass bottle at position: {}, holding: {}",
            //        player.getName().getString(), worldPosition, heldItem.getItem().getDescriptionId());
            return false;
        }

        // LOGGER.debug("[FURBOHE] Player {} holding glass bottle, proceeding with collection at {}",
        //        player.getName().getString(), worldPosition);

        if (!player.isCreative()) {
            heldItem.shrink(1);
            // LOGGER.debug("[FURBOHE] Consumed glass bottle from player {} at {}",
            //        player.getName().getString(), worldPosition);
        } else {
            // LOGGER.debug("[FURBOHE] Creative mode - glass bottle not consumed for player {} at {}",
            //        player.getName().getString(), worldPosition);
        }

        ItemStack resinBottle = new ItemStack(RegisterItems.ITEM_BOTTLE_RESIN.get());
        if (!player.getInventory().add(resinBottle)) {
            player.drop(resinBottle, false);
            // LOGGER.debug("[FURBOHE] Player {} inventory full, dropped resin bottle at {}",
            //        player.getName().getString(), worldPosition);
        } else {
            // LOGGER.debug("[FURBOHE] Added resin bottle to player {} inventory at {}",
             //       player.getName().getString(), worldPosition);
        }

        startCooldown();
        LOGGER.info("[FURBOHE] Successfully collected resin for player {} at position: {}, cooldown started",
                player.getName().getString(), worldPosition);

        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);

        return true;
    }

    public boolean resetCooldownWithSlime(Player player) {
        if (level == null) {
            LOGGER.warn("[FURBOHE] resetCooldownWithSlime called with null level at position: {}", worldPosition);
            return false;
        }

        LOGGER.info("[FURBOHE] Player {} attempting to reset cooldown with slime at position: {}, current cooldown: {}",
                player.getName().getString(), worldPosition, cooldown);

        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.getItem() != Items.SLIME_BALL) {
            // LOGGER.debug("[FURBOHE] Player {} not holding slime ball at position: {}, holding: {}",
            //        player.getName().getString(), worldPosition, heldItem.getItem().getDescriptionId());
            return false;
        }

        if (cooldown <= 0) {
            // LOGGER.debug("[FURBOHE] Cooldown already reset for player {} at position: {}",
            //        player.getName().getString(), worldPosition);
            return false;
        }

        if (!player.isCreative()) {
            heldItem.shrink(1);
            // LOGGER.debug("[FURBOHE] Consumed slime ball from player {} at {}",
            //        player.getName().getString(), worldPosition);
        } else {
            // LOGGER.debug("[FURBOHE] Creative mode - slime ball not consumed for player {} at {}",
             //       player.getName().getString(), worldPosition);
        }

        cooldown = 0;
        LOGGER.info("[FURBOHE] Cooldown reset by player {} at position: {}",
                player.getName().getString(), worldPosition);

        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);

        return true;
    }

    public boolean canCollect() {
        if (level == null) {
            LOGGER.warn("[FURBOHE] canCollect called with null level at position: {}", worldPosition);
            return false;
        }

        boolean result = cooldown == 0 && validateAndUpdate();
        // LOGGER.debug("[FURBOHE] canCollect result at {}: {} (cooldown: {}, isValid: {})",
         //       worldPosition, result, cooldown, isValidPosition);
        return result;
    }

    public void startCooldown() {
        this.cooldown = MAX_COOLDOWN;
        // LOGGER.debug("[FURBOHE] Cooldown started at position: {}, max cooldown: {} ticks",
          //      worldPosition, MAX_COOLDOWN);
        setChanged();
    }

    public int getCooldown() {
        return cooldown;
    }

    public int getCooldownSeconds() {
        return cooldown / 20;
    }

    public float getCooldownProgress() {
        return 1.0f - ((float) cooldown / MAX_COOLDOWN);
    }

    public boolean isValidPosition() {
        return isValidPosition;
    }

    public boolean isValidBiome() {
        if (level == null) {
            LOGGER.warn("[FURBOHE] isValidBiome called with null level at position: {}", worldPosition);
            return false;
        }
        Holder<Biome> biomeHolder = level.getBiome(worldPosition);
        boolean result = biomeHolder.is(BiomeTags.IS_OVERWORLD) &&
                !biomeHolder.is(BiomeTags.IS_DEEP_OCEAN) &&
                !biomeHolder.is(BiomeTags.IS_HILL);
        // LOGGER.debug("[FURBOHE] isValidBiome at {}: {}", worldPosition, result);
        return result;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.cooldown = tag.getInt("cooldown");
        this.isValidPosition = tag.getBoolean("isValidPosition");
        this.validateCounter = tag.getInt("validateCounter");
        // LOGGER.debug("[FURBOHE] Loaded data at position: {} - cooldown: {}, isValid: {}, validateCounter: {}",
        //        worldPosition, cooldown, isValidPosition, validateCounter);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("cooldown", this.cooldown);
        tag.putBoolean("isValidPosition", this.isValidPosition);
        tag.putInt("validateCounter", this.validateCounter);
        // LOGGER.debug("[FURBOHE] Saved data at position: {} - cooldown: {}, isValid: {}, validateCounter: {}",
          //      worldPosition, cooldown, isValidPosition, validateCounter);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        LOGGER.trace("[FURBOHE] Creating update packet for position: {}", worldPosition);
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("cooldown", this.cooldown);
        tag.putBoolean("isValidPosition", this.isValidPosition);
        tag.putInt("validateCounter", this.validateCounter);
        LOGGER.trace("[FURBOHE] Creating update tag for position: {} - cooldown: {}, isValid: {}",
                worldPosition, cooldown, isValidPosition);
        return tag;
    }
}