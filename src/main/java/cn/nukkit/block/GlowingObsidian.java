package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * Created on 2015/11/22 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class GlowingObsidian extends Obsidian {

    public GlowingObsidian() {
        this(0);
    }

    public GlowingObsidian(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return GLOWING_OBSIDIAN;
    }

    @Override
    public String getName() {
        return "Glowing Obsidian";
    }
    
    @Override
    public double getResistance() {
        return 6000;
    }

    @Override
    public int getLightLevel() {
        return 12;
    }
    
    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() > Tool.DIAMOND_PICKAXE) {
            return new int[][]{{Item.GLOWING_OBSIDIAN, 0, 1}};
        } else {
            return new int[0][];
        }
    }
}
