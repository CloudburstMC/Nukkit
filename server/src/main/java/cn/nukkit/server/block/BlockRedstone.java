package cn.nukkit.server.block;

import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemTool;
import cn.nukkit.server.math.BlockFace;
import cn.nukkit.server.util.BlockColor;

/*
 * Created on 2015/12/11 by Pub4Game.
 * Package cn.nukkit.server.block in project Nukkit .
 */
public class BlockRedstone extends BlockSolid {

    public BlockRedstone() {
        this(0);
    }

    public BlockRedstone(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return REDSTONE_BLOCK;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public double getHardness() {
        return 5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return "Redstone BlockType";
    }

    //TODO: redstone

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem()
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.REDSTONE_BLOCK_COLOR;
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    @Override
    public int getWeakPower(BlockFace face) {
        return 15;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}