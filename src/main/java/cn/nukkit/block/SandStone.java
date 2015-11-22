package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SandStone extends Solid {
    public static final int NORMAL = 0;
    public static final int CHISELED = 1;
    public static final int SMOOTH = 2;


    public SandStone() {
        this(0);
    }

    public SandStone(int meta) {
        super(SANDSTONE, meta);
    }

    @Override
    public double getHardness() {
        return 0.8;
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
        if (item.isPickaxe() && item.getTier() >= Tool.TIER_WOODEN) {
            return new int[][]{new int[]{Item.SANDSTONE, this.meta & 0x03, 1}};
        } else {
            return new int[0][];
        }
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }
}
