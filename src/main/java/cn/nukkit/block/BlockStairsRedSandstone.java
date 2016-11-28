package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

/**
 * Created by CreeperFace on 26. 11. 2016.
 */
public class BlockStairsRedSandstone extends BlockStairs {

    public BlockStairsRedSandstone() {
        this(0);
    }

    public BlockStairsRedSandstone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return RED_SANDSTONE_STAIRS;
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
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return "Red Sandstone Stairs";
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new int[][]{
                    {Item.RED_SANDSTONE_STAIRS, this.meta & 0x07, 1}
            };
        } else {
            return new int[0][0];
        }
    }
}