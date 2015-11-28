package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class DiamondOre extends Solid {


    public DiamondOre() {
        this(0);
    }

    public DiamondOre(int meta) {
        super(DIAMOND_ORE, meta);
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return "Diamond Ore";
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= Tool.TIER_IRON) {
            return new int[][]{new int[]{Item.DIAMOND, 0, 1}};
        } else {
            return new int[0][];
        }
    }
}
