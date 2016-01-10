package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class GoldOre extends Solid {


    public GoldOre() {
        this(0);
    }

    public GoldOre(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return GOLD_ORE;
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 5;
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
        if (item.isPickaxe() && item.getTier() >= Tool.TIER_IRON) {
            return new int[][]{new int[]{Item.GOLD_ORE, 0, 1}};
        } else {
            return new int[0][];
        }
    }
}
