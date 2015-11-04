package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CoalOre extends Solid {

    public CoalOre() {
        this(0);
    }

    public CoalOre(int meta) {
        super(COAL_ORE, meta);
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
        return "Coal Ore";
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= Tool.TIER_WOODEN) {
            return new int[][]{new int[]{Item.COAL, 0, 1}};
        } else {
            return new int[0][];
        }
    }
}
