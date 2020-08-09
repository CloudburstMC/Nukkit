package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDaylightDetector;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityDaylightDetector extends BlockEntity {

    public BlockEntityDaylightDetector(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        scheduleUpdate();
    }

    @Override
    public boolean isBlockEntityValid() {
        return getLevelBlock().getId() == BlockID.DAYLIGHT_DETECTOR;
    }

    @Override
    public boolean onUpdate() {
        Block block = getLevelBlock();
        if (block instanceof BlockDaylightDetector) {
            ((BlockDaylightDetector) getBlock()).updatePower();
            return true;
        } else {
            return false;
        }
    }

}
