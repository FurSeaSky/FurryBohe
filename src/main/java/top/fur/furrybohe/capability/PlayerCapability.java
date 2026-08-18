package top.fur.furrybohe.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AutoRegisterCapability
public class PlayerCapability implements INBTSerializable<CompoundTag> {
    // 玩家Object 虽然获取时已经知道是哪位玩家 但是Capability中的函数有可能需要Player的Object
    public Player player;
    // 装师经验
    public int fursuitMakerExperience = 0;
    // 装师等级
    public int fursuitMakerLevel = 0;
    // 经验列表 提前算一下节省算力
    public static final int[] fursuitMakerExperienceArray = new int[80];
    static {
        int nowExp = 0;
        int nowLevel = 0;
        for (int i = 0; i < 80; i++) {
            nowExp += 50 * (nowLevel + 1);
            fursuitMakerExperienceArray[i] = nowExp;
            nowLevel++;
        }
    }

    public PlayerCapability(Player player) {
        this.player = player;
    }

    public static @Nullable PlayerCapability get(@Nullable Player player) {
        return player == null ? null : player.getCapability(PlayerCapabilityProvider.capability).orElse(null);
    }

    public static @NotNull LazyOptional<PlayerCapability> getOptional(@Nullable Player player) {
        return player == null ? LazyOptional.empty() : player.getCapability(PlayerCapabilityProvider.capability);
    }

    public int getFursuitMakerExperience() {
        return fursuitMakerExperience;
    }

    private void calcLevel() {
        fursuitMakerLevel = 0;
        for (int i = 0; i < fursuitMakerExperienceArray.length; i++) {
            if (fursuitMakerExperience >= fursuitMakerExperienceArray[i]) {
                fursuitMakerLevel = i + 1;
            } else {
                break;
            }
        }
    }

    public void setFursuitMakerExperience(int fursuitMakerExperience) {
        this.fursuitMakerExperience = fursuitMakerExperience;
        this.calcLevel();
    }

    public void addFursuitMakerExperience(int experience) {
        this.fursuitMakerExperience += experience;
        this.calcLevel();
    }

    public int getFursuitMakerLevel() {
        return fursuitMakerLevel;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("fursuit_maker_experience", fursuitMakerExperience);
        nbt.putInt("fursuit_maker_level", fursuitMakerLevel);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        fursuitMakerExperience = 0;
        fursuitMakerLevel = 0;
        if (nbt.contains("fursuit_maker_experience")) {
            fursuitMakerExperience = nbt.getInt("fursuit_maker_experience");
        }
        if (nbt.contains("fursuit_maker_level")) {
            fursuitMakerLevel = nbt.getInt("fursuit_maker_level");
        }
    }
}
