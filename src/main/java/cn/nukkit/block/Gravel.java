package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.utils.RGBColor;

import java.util.Random;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Gravel extends Fallable {


    public Gravel() {
        this(0);
    }

    public Gravel(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return GRAVEL;
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

    @Override
    public RGBColor getMapColor() {
        return RGBColor.sandColor;
    }

}
