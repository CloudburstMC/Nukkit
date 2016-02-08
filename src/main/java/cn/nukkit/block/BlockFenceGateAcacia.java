package cn.nukkit.block;

/**
 * Created on 2015/11/23 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockFenceGateAcacia extends BlockFenceGate {
    public BlockFenceGateAcacia() {
        this(0);
    }

    public BlockFenceGateAcacia(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return FENCE_GATE_ACACIA;
    }

    @Override
    public String getName() {
        return "Acacia Fence Gate";
    }
}
