package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class Obsidian extends Solid {

    public Obsidian(){
        this(0);
    }

    public Obsidian(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Obsidian";
    }

    @Override
    public int getId() {
        return OBSIDIAN;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 50;
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= Tool.TIER_DIAMOND) {
            return new int[][]{
                    {Item.OBSIDIAN, 0, 1}
            };
        } else {
            return new int[][]{};
        }
    }
}
