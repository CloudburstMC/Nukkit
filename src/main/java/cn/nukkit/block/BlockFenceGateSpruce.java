package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/11/23 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockFenceGateSpruce extends BlockFenceGate {
    public BlockFenceGateSpruce() {
        this(0);
    }

    public BlockFenceGateSpruce(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return FENCE_GATE_SPRUCE;
    }

    @Override
    public String getName() {
        return "Spruce Fence Gate";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SPRUCE_BLOCK_COLOR;
    }
}
