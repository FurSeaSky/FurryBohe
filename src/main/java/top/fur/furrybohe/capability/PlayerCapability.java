package top.fur.furrybohe.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

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

    public int getFursuitMakerExperience() {
        return fursuitMakerExperience;
    }

    private void calcLevel() {
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
        fursuitMakerExperience = nbt.getInt("fursuit_maker_experience");
        fursuitMakerLevel = nbt.getInt("fursuit_maker_level");
    }
}
