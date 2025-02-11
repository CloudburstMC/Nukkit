package cn.nukkit.block;

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
}
