package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityDaylightDetector extends BlockEntity {

    public BlockEntityDaylightDetector(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBlockEntityValid() {
        int id = level.getBlockIdAt(chunk, (int) x, (int) y, (int) z);
        return id == BlockID.DAYLIGHT_DETECTOR || id == BlockID.DAYLIGHT_DETECTOR_INVERTED;
    }
}
