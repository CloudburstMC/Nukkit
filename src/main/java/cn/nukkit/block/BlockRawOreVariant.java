package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

public abstract class BlockRawOreVariant extends BlockSolid {

    public BlockRawOreVariant() {
    }

    @Override
    public double getHardness() {
        return 5;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.getTier() < this.getToolTier()) {
            return new Item[0];
        }
        return super.getDrops(item);
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_STONE;
    }

    // TODO:
    /*@Override
    public boolean isLavaResistant() {
        return true;
    }*/

}
