package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

public class BlockBricksEndStone extends BlockSolid {

    public BlockBricksEndStone(int id, int meta) {
        super(id, meta);
    }

    @Override
    public String getName() {
        return "End Stone Bricks";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 0.8;
    }

    @Override
    public double getResistance() {
        return 4;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    Item.get(Item.END_BRICKS, 0, 1)
            };
        } else {
            return new Item[0];
        }
    }
}
