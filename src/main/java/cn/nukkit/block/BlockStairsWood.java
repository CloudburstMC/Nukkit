package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/11/25 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockStairsWood extends BlockStairs {
    public BlockStairsWood() {
        this(0);
    }

    public BlockStairsWood(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WOOD_STAIRS;
    }

    @Override
    public String getName() {
        return "Wood Stairs";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }
    @Override
    public Item[] getDrops(Item item) {
         return new Item[]{
            toItem()
            };
    }
}
