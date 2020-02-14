package cn.nukkit.blockentity.impl;

import cn.nukkit.block.BlockIds;
import cn.nukkit.blockentity.Banner;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.utils.BannerPattern;
import cn.nukkit.utils.DyeColor;
import com.google.common.collect.ImmutableList;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class BannerBlockEntity extends BaseBlockEntity implements Banner {

    private final List<BannerPattern> patterns = new ArrayList<>();
    private DyeColor base = DyeColor.WHITE;
    private int type;

    public BannerBlockEntity(BlockEntityType<?> type, Chunk chunk, Vector3i position) {
        super(type, chunk, position);
    }

    @Override
    public boolean isValid() {
        return this.getBlock().getId() == BlockIds.WALL_BANNER || this.getBlock().getId() == BlockIds.STANDING_BANNER;
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        tag.listenForInt("Base", this::setBase);
        tag.listenForInt("Type", this::setBannerType);

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

    @Override
    public DyeColor getBase() {
        return this.base;
    }

    private void setBase(int base) {
        this.base = DyeColor.getByDyeData(base);
    }

    @Override
    public void setBase(DyeColor color) {
        this.base = checkNotNull(color, "color");
    }

    @Override
    public int getBannerType() {
        return type;
    }

    @Override
    public void setBannerType(int type) {
        this.type = type;
    }

    @Override
    public void addPattern(BannerPattern pattern) {
        this.patterns.add(pattern);
    }

    @Override
    public BannerPattern getPattern(int index) {
        return this.patterns.get(index);
    }

    @Override
    public ImmutableList<BannerPattern> getPatterns() {
        return ImmutableList.copyOf(this.patterns);
    }

    @Override
    public void removePattern(int index) {
        this.patterns.remove(index);
    }

    @Override
    public boolean isSpawnable() {
        return true;
    }
}
