package cn.nukkit.block;

public class BlockFenceGateCrimson extends BlockFenceGate {

    public BlockFenceGateCrimson() {
        this(0);
    }

    public BlockFenceGateCrimson(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Crimson Fence Gate";
    }

    @Override
    public int getId() {
        return CRIMSON_FENCE_GATE;
    }
}
