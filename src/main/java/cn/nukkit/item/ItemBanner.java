package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.utils.BannerPattern;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.Identifier;
import com.google.common.collect.ImmutableList;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PetteriM1
 */
public class ItemBanner extends Item {

    public ItemBanner(Identifier id) {
        super(id);
    }

    private final List<BannerPattern> patterns = new ArrayList<>();
    private DyeColor base = DyeColor.WHITE;
    private int type;

    @Override
    public Block getBlock() {
        return Block.get(BlockIds.STANDING_BANNER);
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        tag.listenForInt("Base", v -> this.base = DyeColor.getByDyeData(v));
        tag.listenForInt("Type", v -> this.type = v);

        tag.listenForList("Patterns", CompoundTag.class, patternTags -> {
            for (CompoundTag patternTag : patternTags) {
                String pattern = patternTag.getString("Pattern");
                DyeColor color = DyeColor.getByDyeData(patternTag.getInt("Color"));
                this.patterns.add(new BannerPattern(BannerPattern.Type.getByName(pattern), color));
            }
        });
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        tag.intTag("Base", this.base.getDyeData());
        tag.intTag("Type", this.type);

        List<CompoundTag> patternsTag = new ArrayList<>();
        for (BannerPattern pattern : this.patterns) {
            patternsTag.add(CompoundTag.builder().
                    intTag("Color", pattern.getColor().getDyeData() & 0x0f).
                    stringTag("Pattern", pattern.getType().getName())
                    .buildRootTag());
        }
        tag.listTag("Patterns", CompoundTag.class, patternsTag);
    }

    public DyeColor getBaseColor() {
        return this.base;
    }

    public void setBaseColor(DyeColor color) {
        this.base = color;
        this.setMeta(color.getDyeData() & 0x0f);
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public DyeColor getBase() {
        return this.base;
    }

    public void setBase(DyeColor color) {
        this.setMeta(color.getDyeData());
    }

    public int getBannerType() {
        return type;
    }

    public void setBannerType(int type) {
        this.type = type;
    }

    public void addPattern(BannerPattern pattern) {
        this.patterns.add(pattern);
    }

    public BannerPattern getPattern(int index) {
        return this.patterns.get(index);
    }

    public ImmutableList<BannerPattern> getPatterns() {
        return ImmutableList.copyOf(this.patterns);
    }

    public void removePattern(int index) {
        this.patterns.remove(index);
    }

    @Override
    protected void onMetaChange(int newMeta) {
        this.base = DyeColor.getByDyeData(newMeta & 0xf);
    }
}
