package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityMusic extends BlockEntitySpawnable {

    public BlockEntityMusic(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        if (!this.namedTag.contains("note")) {
            this.namedTag.putByte("note", 0);
        }

        super.initBlockEntity();
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getBlock().getId() == Block.NOTEBLOCK;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return getDefaultCompound(this, MUSIC)
                .putByte("note", this.namedTag.getByte("note"));
    }

    public void changePitch() {
        this.namedTag.putByte("note", Math.abs(this.namedTag.getByte("note") + 1) % 25);
        setDirty();
    }
}
