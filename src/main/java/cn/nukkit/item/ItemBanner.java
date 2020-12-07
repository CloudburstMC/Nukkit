package cn.nukkit.item;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.BannerPattern;
import cn.nukkit.utils.DyeColor;

/**
 * @author PetteriM1
 */
public class ItemBanner extends Item {

    public ItemBanner() {
        this(0);
    }

    public ItemBanner(Integer meta) {
        this(meta, 1);
    }

    public ItemBanner(Integer meta, int count) {
        super(BANNER, meta, count, "Banner");
        this.block = Block.get(Block.STANDING_BANNER);
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }

    public int getBaseColor() {
        return this.getDamage() & 0x0f;
    }

    public void setBaseColor(DyeColor color) {
        this.setDamage(color.getDyeData() & 0x0f);
    }

    public int getType() {
        return this.getNamedTag().getInt("Type");
    }

    public void setType(int type) {
        CompoundTag tag = this.hasCompoundTag() ? this.getNamedTag() : new CompoundTag();
        tag.putInt("Type", type);
        this.setNamedTag(tag);
    }

    public void addPattern(BannerPattern pattern) {
        CompoundTag tag = this.hasCompoundTag() ? this.getNamedTag() : new CompoundTag();
        ListTag<CompoundTag> patterns = tag.getList("Patterns", CompoundTag.class);
        patterns.add(new CompoundTag("").
                putInt("Color", pattern.getColor().getDyeData() & 0x0f).
                putString("Pattern", pattern.getType().getName()));
        tag.putList(patterns);
        this.setNamedTag(tag);
    }

    public BannerPattern getPattern(int index) {
        CompoundTag tag = this.hasCompoundTag() ? this.getNamedTag() : new CompoundTag();
        return BannerPattern.fromCompoundTag(tag.getList("Patterns").size() > index && index >= 0 ? tag.getList("Patterns", CompoundTag.class).get(index) : new CompoundTag());
    }

    public void removePattern(int index) {
        CompoundTag tag = this.hasCompoundTag() ? this.getNamedTag() : new CompoundTag();
        ListTag<CompoundTag> patterns = tag.getList("Patterns", CompoundTag.class);
        if(patterns.size() > index && index >= 0) {
            patterns.remove(index);
        }
        this.setNamedTag(tag);
    }

    public int getPatternsSize() {
        return (this.hasCompoundTag() ? this.getNamedTag() : new CompoundTag()).getList("Patterns").size();
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean hasPattern() {
        return (this.hasCompoundTag() ? this.getNamedTag() : new CompoundTag()).contains("Patterns");
    }

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", 
            reason = "Does nothing, used to do a backward compatibility but the content and usage were removed by Cloudburst")
    public void correctNBT() {

    }
}
