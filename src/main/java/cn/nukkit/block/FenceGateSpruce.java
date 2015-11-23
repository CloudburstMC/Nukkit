package cn.nukkit.block;

/**
 * Created on 2015/11/23 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class FenceGateSpruce extends FenceGate {
    public FenceGateSpruce(){
        this(0);
    }

    public FenceGateSpruce(int meta) {
        super(FENCE_GATE_SPRUCE ,meta);
    }

    @Override
    public String getName() {
        return "Spruce Fence Gate";
    }
}
