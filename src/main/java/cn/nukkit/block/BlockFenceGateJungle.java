package cn.nukkit.block;

import cn.nukkit.item.Item;

/**
 * Created on 2015/11/23 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
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

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                Item.get(Item.FENCE_GATE_JUNGLE, 0, 1)
        };
    }
}
