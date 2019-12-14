package cn.nukkit.block;

import cn.nukkit.item.Item;

/**
 * Created on 2015/11/23 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockFenceGateBirch extends BlockFenceGate {
    public BlockFenceGateBirch(int id, int meta) {
        super(id, meta);
    }

    @Override
    public String getName() {
        return "Birch Fence Gate";
    }

    @Override
    public Item toItem() {
        return Item.get(Item.FENCE_GATE_BIRCH, 0, 1);
    }
}
