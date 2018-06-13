package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

public class BlockPistonExtension extends BlockTransparent {

    public BlockPistonExtension() {
    }

    @Override
    public int getId() {
        return PISTON_EXTENSION;
    }

    @Override
    public String getName() {
        return "Piston Extension";
    }

    @Override
    public double getHardness() {
        return 0.1;
    }

    @Override
    public double getResistance() {
        return 0.1;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_NONE;
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
