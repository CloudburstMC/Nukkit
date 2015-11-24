package cn.nukkit.block;

/**
 * Created on 2015/11/23 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class FenceGateDarkOak extends FenceGate {
    public FenceGateDarkOak() {
        this(0);
    }

    public FenceGateDarkOak(int meta) {
        super(FENCE_GATE_DARK_OAK, meta);
    }

    @Override
    public String getName() {
        return "Dark Oak Fence Gate";
    }
}
