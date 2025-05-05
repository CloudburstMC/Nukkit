package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockFenceGateMangrove extends BlockFenceGate {

    public BlockFenceGateMangrove() {
        this(0);
    }

    public BlockFenceGateMangrove(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Mangrove Fence Gate";
    }

    @Override
    public int getId() {
        return MANGROVE_FENCE_GATE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }
}
