package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCherryFenceGate extends BlockFenceGate {

    public BlockCherryFenceGate() {
        this(0);
    }

    public BlockCherryFenceGate(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CHERRY_FENCE_GATE;
    }

    @Override
    public String getName() {
        return "Cherry Fence Gate";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WHITE_TERRACOTA_BLOCK_COLOR;
    }
}
