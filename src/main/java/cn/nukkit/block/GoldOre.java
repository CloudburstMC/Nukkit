package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class GoldOre extends Solid {

    protected int id = GOLD_ORE;

    public GoldOre() {
        super(GOLD_ORE);
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
        return "Gold Ore";
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= 4) {
            return new int[][]{new int[]{Item.GOLD_ORE, 0, 1}};
        } else {
            return new int[0][];
        }
    }
}
