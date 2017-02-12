package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

/**
 * Created by CreeperFace on 26. 11. 2016.
 */
public class BlockDoubleSlabRedSandstone extends BlockSolid {

    public BlockDoubleSlabRedSandstone() {
        this(0);
    }

    public BlockDoubleSlabRedSandstone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DOUBLE_RED_SANDSTONE_SLAB;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "Red Sandstone",
                "Purpur",
                "",
                "",
                "",
                "",
                "",
                ""
        };

        return "Double " + names[this.meta & 0x07] + " Slab";
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new int[][]{
                    {Item.RED_SANDSTONE_SLAB, this.meta & 0x07, 2}
            };
        } else {
            return new int[0][0];
        }
    }
}