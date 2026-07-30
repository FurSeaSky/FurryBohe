package top.fur.furrybohe.base;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import top.fur.furrybohe.config.repo_configs.ModInfo;

import java.util.List;

public abstract class BaseCrystal extends Item{
    public enum CrystalLevel {
        D, C, B, A, S, SP, SPP,NONE;

        private static final String PREFIX = "crystal.level.";

        @Override
        public String toString() {
            return PREFIX + this.name().toLowerCase();
        }

        public Component getDisplayName() {
            return Component.translatable(toString());
        }
    }

    public enum CrystalPart {
        ANY, ALL, HEAD, BODY, LEG, PAW,NONE;

        private static final String PREFIX = "crystal.part.";

        @Override
        public String toString() {
            return PREFIX + this.name().toLowerCase();
        }

        public Component getDisplayName() {
            return Component.translatable(toString());
        }
    }

    public enum CrystalBuff {
        POSITIVE, NEGATIVE, NEUTRAL;

        private static final String PREFIX = "crystal.buff.";

        @Override
        public String toString() {
            return PREFIX + this.name().toLowerCase();
        }

        public Component getDisplayName() {
            return Component.translatable(toString());
        }
    }
    public final CrystalLevel level;
    public final CrystalPart part;
    public final CrystalBuff buff;
    public final String id;
    public final String description;
    public String name;

    public BaseCrystal(Properties properties, CrystalLevel level, CrystalPart part, CrystalBuff buff, String id, String description) {
        super(properties.stacksTo(16));
        this.level = level;
        this.part = part;
        this.buff = buff;
        this.id = id;
        this.description = description;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.literal(description));
        list.add(Component.translatable("text." + ModInfo.MODID + "."+ id + ".description.level",this.level.getDisplayName().getString()));
        list.add(Component.translatable("text." + ModInfo.MODID + "."+ id + ".description.part" ,this.part.getDisplayName().getString()));
        list.add(Component.translatable("text." + ModInfo.MODID + "."+ id + ".description.buff" ,this.buff.getDisplayName().getString()));
    }
    public void onRun(Player player){};
    public void onTick(Player player){};
}
