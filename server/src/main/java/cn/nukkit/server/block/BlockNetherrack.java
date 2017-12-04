package cn.nukkit.server.block;

import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemTool;
import cn.nukkit.server.utils.BlockColor;

/**
 * Created on 2015/12/26 by Pub4Game.
 * Package cn.nukkit.server.block in project Nukkit .
 */
public class BlockNetherrack extends BlockSolid {

    public BlockNetherrack() {
        this(0);
    }

    public BlockNetherrack(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return NETHERRACK;
    }

    @Override
    public double getResistance() {
        return 2;
    }

    @Override
    public double getHardness() {
        return 0.4;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return "Netherrack";
    }

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
        return BlockColor.NETHERRACK_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

}
