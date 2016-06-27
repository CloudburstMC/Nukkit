package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

/**
 * Created on 2015/11/22 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockStonecutter extends BlockSolid {

    public BlockStonecutter() {
        this(0);
    }

    public BlockStonecutter(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STONECUTTER;
    }

    @Override
    public String getName() {
        return "Stonecutter";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getResistance() {
        return 17.5;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{
                {Item.STONECUTTER, 0, 1}
        };
    }
}
