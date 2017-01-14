package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

public class BlockBricksEndStone extends BlockSolid {

    public BlockBricksEndStone() {
        this(0);
    }

    public BlockBricksEndStone(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "End Stone Bricks";
    }

    @Override
    public int getId() {
        return END_BRICKS;
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
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new int[][]{
                    {Item.END_BRICKS, 0, 1}
            };
        } else {
            return new int[0][0];
        }
    }
}
