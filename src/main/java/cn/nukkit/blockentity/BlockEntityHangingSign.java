package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityHangingSign extends BlockEntity {

    public BlockEntityHangingSign(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBlockEntityValid() {
        int id = level.getBlockIdAt(chunk, (int) x, (int) y, (int) z);
        return id == BlockID.ACACIA_HANGING_SIGN || id == BlockID.BAMBOO_HANGING_SIGN || id == BlockID.BIRCH_HANGING_SIGN ||
                id == BlockID.CHERRY_HANGING_SIGN || id == BlockID.CRIMSON_HANGING_SIGN || id == BlockID.WARPED_HANGING_SIGN ||
                id == BlockID.MANGROVE_HANGING_SIGN || id == BlockID.DARK_OAK_HANGING_SIGN || id == BlockID.OAK_HANGING_SIGN ||
                id == BlockID.JUNGLE_HANGING_SIGN || id == BlockID.SPRUCE_HANGING_SIGN;
    }
}
