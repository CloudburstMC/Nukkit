package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class Cobblestone extends Solid {

    public Cobblestone() {
        this(0);
    }

    public Cobblestone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return COBBLESTONE;
    }

    @Override
    public double getHardness() {
        return 2.0;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return "Cobblestone";
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= Tool.TIER_WOODEN) {
            return new int[][]{new int[]{Item.COBBLESTONE, 0, 1}};
        } else {
            return new int[0][];
        }
    }

}
