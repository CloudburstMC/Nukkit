package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockBambooFenceGate extends BlockFenceGate {

    public BlockBambooFenceGate() {
        this(0);
    }

    public BlockBambooFenceGate(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BAMBOO_FENCE_GATE;
    }

    @Override
    public String getName() {
        return "Bamboo Fence Gate";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }
}
