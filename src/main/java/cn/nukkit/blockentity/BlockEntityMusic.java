package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityMusic extends BlockEntity {

    public BlockEntityMusic(Chunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        if (!this.namedTag.contains("note")) {
            this.namedTag.putByte("note", 0);
        }
        if (!this.namedTag.contains("powered")) {
            this.namedTag.putBoolean("powered", false);
        }

        super.initBlockEntity();
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getBlock().getId() == Block.NOTEBLOCK;
    }

    public void changePitch() {
        this.namedTag.putByte("note", (this.namedTag.getByte("note") + 1) % 25);
    }

    public int getPitch() {
        return this.namedTag.getByte("note");
    }

    public void setPowered(boolean powered) {
        this.namedTag.putBoolean("powered", powered);
    }

    public boolean isPowered() {
        return this.namedTag.getBoolean("powered");
    }
}
