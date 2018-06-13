package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

public class BlockNetherReactor extends BlockSolid {

    public BlockNetherReactor() {
    }

    @Override
    public int getId() {
        return NETHER_REACTOR;
    }

    @Override
    public String getName() {
        return "Nether Reactor";
    }

    @Override
    public double getHardness() {
        return 30;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
