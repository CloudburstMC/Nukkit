package cn.nukkit.block;

import cn.nukkit.item.Item;

public class BlockFenceGateOak extends BlockFenceGate {
    public BlockFenceGateOak(int id, int meta) {
        super(id, meta);
    }

    @Override
    public String getName() {
        return "Oak Fence Gate";
    }

    @Override
    public Item toItem() {
        return Item.get(Item.FENCE_GATE_OAK, 0, 1);
    }
}