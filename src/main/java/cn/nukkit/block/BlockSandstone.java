package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockSandstone extends BlockSolid {
    public static final int NORMAL = 0;
    public static final int CHISELED = 1;
    public static final int SMOOTH = 2;

    public BlockSandstone() {
        this(0);
    }

    public BlockSandstone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SANDSTONE;
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
    public String getName() {
        String[] names = new String[]{
                "Sandstone",
                "Chiseled Sandstone",
                "Smooth Sandstone",
                ""
        };

        return names[this.meta & 0x03];
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new int[][]{new int[]{Item.SANDSTONE, this.meta & 0x03, 1}};
        } else {
            return new int[0][];
        }
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }
}
