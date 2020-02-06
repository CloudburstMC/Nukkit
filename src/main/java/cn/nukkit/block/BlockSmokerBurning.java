package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.utils.Identifier;

public class BlockSmokerBurning extends BlockFurnaceBurning {
    public BlockSmokerBurning(Identifier id) {
        super(id);
    }

    @Override
    protected String getBlockEntityID() {
        return BlockEntity.SMOKER;
    }
}
