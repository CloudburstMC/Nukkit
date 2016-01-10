package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class Gold extends Solid {


    public Gold() {
        this(0);
    }

    public Gold(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return GOLD_BLOCK;
    }

    @Override
    public String getName() {
        return "Gold Block";
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= Tool.TIER_IRON) {
            return new int[][]{new int[]{Item.GOLD_BLOCK, 0, 1}};
        } else {
            return new int[0][];
        }
    }
}
