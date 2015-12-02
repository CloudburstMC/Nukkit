package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class MossStone extends Solid {

    public MossStone() {
        this(0);
    }

    public MossStone(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Moss Stone";
    }

    @Override
    public int getId() {
        return MOSS_STONE;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe()) {
            return new int[][]{
                    {Item.MOSS_STONE, this.meta, 1}
            };
        } else {
            return new int[][]{};
        }
    }
}
