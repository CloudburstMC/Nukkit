package cn.nukkit.block;

/**
 * Created on 2015/11/23 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class FenceGateAcacia extends FenceGate {
    public FenceGateAcacia() {
        this(0);
    }

    public FenceGateAcacia(int meta) {
        super(FENCE_GATE_ACACIA, meta);
    }

    @Override
    public String getName() {
        return "Acacia Fence Gate";
    }
}
