package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

import java.util.Random;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Gravel extends Solid {

    protected int id = GRAVEL;

    public Gravel() {
        super(GRAVEL);
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_SHOVEL;
    }

    @Override
    public String getName() {
        return "Gravel";
    }

    @Override
    public int[][] getDrops(Item item) {
        if (new Random().nextInt(9) == 0) {
            return new int[][]{new int[]{Item.FLINT, 0, 1}};
        } else {
            return new int[][]{new int[]{Item.GRAVEL, 0, 1}};
        }
    }
}
