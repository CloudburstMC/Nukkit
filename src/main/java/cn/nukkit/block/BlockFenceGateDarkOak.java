package cn.nukkit.block;

/**
 * Created on 2015/11/23 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockFenceGateDarkOak extends BlockFenceGate {
    public BlockFenceGateDarkOak() {
        this(0);
    }

    public BlockFenceGateDarkOak(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return FENCE_GATE_DARK_OAK;
    }

    @Override
    public String getName() {
        return "Dark Oak Fence Gate";
    }
}
