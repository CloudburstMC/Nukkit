package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

import java.util.Random;

/*
 * Created on 2015/12/11 by Pub4Game.
 * Package package cn.nukkit.block in project Nukkit .
*/

public class Melon extends Transparent {

    public Melon() {
        this(0);
    }

    public Melon(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return MELON_BLOCK;
    }

    public String getName() {
        return "Melon Block";
    }

    public double getHardness() {
        return 1;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{new int[]{Item.MELON_SLICE, 0, new Random().nextInt(3) + 4}};
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_AXE;
    }
}
