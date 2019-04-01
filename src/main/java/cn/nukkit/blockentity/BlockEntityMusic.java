package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityMusic extends BlockEntity {

    public BlockEntityMusic(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        if (!this.namedTag.contains("note")) {
            this.namedTag.putByte("note", 0);
        }
        if (!this.namedTag.contains("powered")) {
            this.namedTag.putBoolean("note", false);
        }

        super.initBlockEntity();
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getBlock().getId() == Block.NOTEBLOCK;
    }

    public void changePitch() {
        this.namedTag.putByte("note", Math.abs(this.namedTag.getByte("note") + 1) % 25);
    }
}
