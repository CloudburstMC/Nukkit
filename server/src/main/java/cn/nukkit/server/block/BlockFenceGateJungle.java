package cn.nukkit.server.block;

/**
 * Created on 2015/11/23 by xtypr.
 * Package cn.nukkit.server.block in project Nukkit .
 */
public class BlockFenceGateJungle extends BlockFenceGate {
    public BlockFenceGateJungle() {
        this(0);
    }

    public BlockFenceGateJungle(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return FENCE_GATE_JUNGLE;
    }

    @Override
    public String getName() {
        return "Jungle Fence Gate";
    }
}
