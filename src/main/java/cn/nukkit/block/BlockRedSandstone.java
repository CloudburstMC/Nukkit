package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

/**
 * Created by CreeperFace on 26. 11. 2016.
 */
public class BlockRedSandstone extends BlockSandstone {

    public BlockRedSandstone() {
        this(0);
    }

    public BlockRedSandstone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return RED_SANDSTONE;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "Red Sandstone",
                "Chiseled Red Sandstone",
                "Smooth Red Sandstone",
                ""
        };

        return names[this.meta & 0x03];
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new int[][]{
                    {Item.RED_SANDSTONE, this.meta & 0x03, 1}
            };
        } else {
            return new int[0][0];
        }
    }
}
