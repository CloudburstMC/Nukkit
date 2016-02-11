package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Created by Snake1999 on 2016/2/4.
 * Package cn.nukkit.blockentity in project Nukkit.
 */
public class BlockEntityFlowerPot extends BlockEntitySpawnable {
    public BlockEntityFlowerPot(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        if (!nbt.contains("item")) {
            nbt.putShort("item", 0);
        }
        if (!nbt.contains("mData")) {
            nbt.putInt("mData", 0);
        }
        this.namedTag = nbt;
    }

    @Override
    public boolean isBlockEntityValid() {
        int blockID = getBlock().getId();
        return blockID == Block.FLOWER_POT_BLOCK;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return new CompoundTag()
                .putString("id", BlockEntity.FLOWER_POT)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .put("item", this.namedTag.get("item"))
                .put("mData", this.namedTag.get("mData"));
    }

    @Override
    public String getSaveId() {
        return FLOWER_POT;
    }
}
