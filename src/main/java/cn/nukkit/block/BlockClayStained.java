package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockClayStained extends BlockSolid {

    public BlockClayStained() {
        this(0);
    }

    public BlockClayStained(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return ItemDye.getColorName(meta) + " Stained Clay";
    }

    @Override
    public int getId() {
        return STAINED_CLAY;
    }

    @Override
    public double getHardness() {
        return 1.25;
    }

    @Override
    public double getResistance() {
        return 0.75;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new int[][]{{Item.STAINED_CLAY, this.meta, 1}};
        } else {
            return new int[0][0];
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.getDyeColor(meta);
    }

}
