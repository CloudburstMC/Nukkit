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

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag c = getDefaultCompound(this, BANNER)
                .putInt("Base", getBaseColor())
                .putList(namedTag.getList("Patterns"));

        if (this.namedTag.contains("Type")) {
            c.put("Type", this.namedTag.get("Type"));
        }

        return c;
    }
}
