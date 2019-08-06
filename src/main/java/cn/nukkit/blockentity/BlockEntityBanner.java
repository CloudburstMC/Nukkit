package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityBanner extends BlockEntitySpawnable {

    public BlockEntityBanner(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getBlock().getId() == Block.WALL_BANNER || this.getBlock().getId() == Block.STANDING_BANNER;
    }

    @Override
    public String getName() {
        return "Banner";
    }

    public int getBaseColor() {
        return this.namedTag.getInt("Base");
    }

    public void setBaseColor(int color) {
        this.namedTag.putInt("Base", color & 0x0f);
    }

    public int getType() {
        return this.namedTag.getInt("Type");
    }

    public void setType(int type) {
        this.namedTag.putInt("Type", type);
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return getDefaultCompound(this, BANNER)
                .putInt("Base", getBaseColor())
                .putList(namedTag.getList("Patterns"))
                .putInt("Type", getType());
    }
}
