package cn.nukkit.blockentity;

import cn.nukkit.block.BlockDaylightDetector;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityDaylightDetector extends BlockEntity {

    public BlockEntityDaylightDetector(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBlockEntityValid() {
        return true;
    }

    @Override
    public boolean onUpdate() {
        ((BlockDaylightDetector)getBlock()).updatePower();
        return true;
    }

}