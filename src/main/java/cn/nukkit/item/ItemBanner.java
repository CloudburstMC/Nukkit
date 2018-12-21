package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;

/**
 * Created by PetteriM1
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
        return this.getNamedTag().getInt("Base");
    }

    public void setBaseColor(int color) {
        this.getNamedTag().putInt("Base", color & 0x0f);
    }

    public void correctNBT() {
        CompoundTag tag = this.getNamedTag() != null ? this.getNamedTag() : new CompoundTag();
        if (!tag.contains("Base") || !(tag.get("Base") instanceof IntTag)) {
            tag.putInt("Base", this.meta);
        }

        this.setNamedTag(tag);
    }
}
