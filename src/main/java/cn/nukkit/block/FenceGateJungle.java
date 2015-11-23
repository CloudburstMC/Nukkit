package cn.nukkit.block;

/**
 * Created on 2015/11/23 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class FenceGateJungle extends FenceGate {
    public FenceGateJungle(){
        this(0);
    }

    public FenceGateJungle(int meta) {
        super(FENCE_GATE_JUNGLE, meta);
    }

    @Override
    public String getName() {
        return "Jungle Fence Gate";
    }
}
