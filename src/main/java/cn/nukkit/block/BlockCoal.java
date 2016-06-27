package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

/**
 * Created on 2015/11/24 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockCoal extends BlockSolid {
    public BlockCoal() {
        this(0);
    }

    public BlockCoal(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return COAL_BLOCK;
    }

    @Override
    public double getHardness() {
        return 5;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 5;
    }

    @Override
    public String getName() {
        return "Block of Coal";
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new int[][]{
                    {Item.COAL_BLOCK, 0, 1}
            };
        } else {
            return new int[0][0];
        }
    }
}
