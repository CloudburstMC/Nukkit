package cn.nukkit.block;

public class BlockFenceGateWarped extends BlockFenceGate {

    public BlockFenceGateWarped() {
        this(0);
    }

    public BlockFenceGateWarped(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Warped Fence Gate";
    }

    @Override
    public int getId() {
        return WARPED_FENCE_GATE;
    }
}
