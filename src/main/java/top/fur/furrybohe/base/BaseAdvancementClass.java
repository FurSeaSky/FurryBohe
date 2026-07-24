package top.fur.furrybohe.base;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.data.ExistingFileHelper;
import top.fur.furrybohe.config.repo_configs.ModInfo;

import java.util.function.Consumer;

public abstract class BaseAdvancementClass {

    protected final String id;
    protected final String title;
    protected final String description;
    protected final ItemStack icon;
    protected final FrameType frameType;
    protected final boolean showToast;
    protected final boolean announceToChat;
    protected final boolean hidden;
    protected final ResourceLocation background;
    protected BaseAdvancementClass parent;
    protected Advancement.Builder builder;
    protected Advancement savedAdvancement;  // 存储已保存的 Advancement 实例

    public BaseAdvancementClass(String id, ItemStack icon) {
        this(id, icon, FrameType.TASK, true, true, false, null);
    }

    public BaseAdvancementClass(String id,ItemStack icon,
                                FrameType frameType, boolean showToast, boolean announceToChat,
                                boolean hidden, ResourceLocation background) {
        this.id = id;
        this.title = "advancement."+ModInfo.MODID+"."+id;
        this.description = "advancement."+ModInfo.MODID+"."+id+".description";
        this.icon = icon;
        this.frameType = frameType;
        this.showToast = showToast;
        this.announceToChat = announceToChat;
        this.hidden = hidden;
        this.background = background;
    }

    // 初始化 Builder（不保存）
    public void initBuilder() {
        builder = Advancement.Builder.advancement();

        builder.display(
                new DisplayInfo(
                        icon,
                        Component.literal(title),
                        Component.literal(description),
                        background,
                        frameType,
                        showToast,
                        announceToChat,
                        hidden
                )
        );

        buildCriteriaAndRewards();
    }

    // 子类实现：添加触发条件和奖励
    protected abstract void buildCriteriaAndRewards();

    // 保存进度（自动处理父子关系）
    public void save(Consumer<Advancement> saver, ExistingFileHelper existingFileHelper) {
        if (builder == null) {
            initBuilder();
        }

        // 如果有父进度，设置父关系
        if (parent != null) {
            // 如果父进度还没保存，先递归保存父进度
            builder.parent(parent.savedAdvancement);
        }

        // 保存当前进度并存储结果
        savedAdvancement = builder.save(saver, new ResourceLocation(ModInfo.MODID, id), existingFileHelper);
    }

    public Advancement.Builder getBuilder() {
        return builder;
    }

    public void setParent(BaseAdvancementClass parent) {
        this.parent = parent;
    }

    public String getId() {
        return id;
    }

    public Advancement getSavedAdvancement() {
        return savedAdvancement;
    }
}