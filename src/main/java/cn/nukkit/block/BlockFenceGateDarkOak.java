package cn.nukkit.block;

import cn.nukkit.item.Item;

/**
 * Created on 2015/11/23 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockFenceGateDarkOak extends BlockFenceGate {
    public BlockFenceGateDarkOak(int id, int meta) {
        super(id, meta);
    }

    @Override
    public String getName() {
        return "Dark Oak Fence Gate";
    }

    @Override
    public Item toItem() {
        return Item.get(Item.FENCE_GATE_DARK_OAK, 0, 1);
    }
}
