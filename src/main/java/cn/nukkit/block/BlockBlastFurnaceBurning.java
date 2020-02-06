package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.utils.Identifier;

public class BlockBlastFurnaceBurning extends BlockFurnaceBurning {
    public BlockBlastFurnaceBurning(Identifier id) {
        super(id);
    }

    @Override
    protected String getBlockEntityID() {
        return BlockEntity.BLAST_FURNACE;
    }
}
