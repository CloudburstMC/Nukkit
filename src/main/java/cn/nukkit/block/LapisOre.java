package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

import java.util.Random;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class LapisOre extends Solid {

    protected int id = LAPIS_ORE;

    public LapisOre() {
        super(LAPIS_ORE);
    }

    public LapisOre(int meta) {
        super(LAPIS_ORE, 0);
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
        return "Lapis Lazuli Ore";
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= 3) {
            return new int[][]{new int[]{Item.DYE, 0, new Random().nextInt(4) + 4}};
        } else {
            return new int[0][];
        }
    }

}
