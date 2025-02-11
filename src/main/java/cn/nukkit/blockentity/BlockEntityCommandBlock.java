package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityCommandBlock extends BlockEntity {

    public BlockEntityCommandBlock(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBlockEntityValid() {
        int id = level.getBlockIdAt(chunk, (int) x, (int) y, (int) z);
        return id == BlockID.COMMAND_BLOCK || id == BlockID.CHAIN_COMMAND_BLOCK || id == BlockID.REPEATING_COMMAND_BLOCK;
    }
}
