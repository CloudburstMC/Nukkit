package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.BannerPattern;
import cn.nukkit.utils.DyeColor;

public class BlockEntityBanner extends BlockEntitySpawnable {

    public int color;

    public BlockEntityBanner(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        if (!this.namedTag.contains("color")) {
            this.namedTag.putByte("color", 0);
        }

        this.color = this.namedTag.getByte("color");

        super.initBlockEntity();
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getBlock().getId() == Block.WALL_BANNER || this.getBlock().getId() == Block.STANDING_BANNER;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putByte("color", this.color);
    }

    @Override
    public String getName() {
        return "Banner";
    }

    public int getBaseColor() {
        return this.namedTag.getInt("Base");
    }

    public void setBaseColor(DyeColor color) {
        this.namedTag.putInt("Base", color.getDyeData() & 0x0f);
    }

    public int getType() {
        return this.namedTag.getInt("Type");
    }

    public void setType(int type) {
        this.namedTag.putInt("Type", type);
    }

    public void addPattern(BannerPattern pattern) {
        ListTag<CompoundTag> patterns = this.namedTag.getList("Patterns", CompoundTag.class);
        patterns.add(new CompoundTag("").
                putInt("Color", pattern.getColor().getDyeData() & 0x0f).
                putString("Pattern", pattern.getType().getName()));
        this.namedTag.putList(patterns);
    }

    public BannerPattern getPattern(int index) {
        return BannerPattern.fromCompoundTag(this.namedTag.getList("Patterns").size() > index && index >= 0 ? this.namedTag.getList("Patterns", CompoundTag.class).get(index) : new CompoundTag());
    }

    public void removePattern(int index) {
        ListTag<CompoundTag> patterns = this.namedTag.getList("Patterns", CompoundTag.class);
        if(patterns.size() > index && index >= 0) {
            patterns.remove(index);
        }
    }

    public int getPatternsSize() {
        return this.namedTag.getList("Patterns").size();
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return getDefaultCompound(this, BANNER)
                .putInt("Base", getBaseColor())
                .putList(this.namedTag.getList("Patterns"))
                .putInt("Type", getType())
                .putByte("color", this.color);
    }

    public DyeColor getDyeColor() {
        return DyeColor.getByWoolData(color);
    }
}
