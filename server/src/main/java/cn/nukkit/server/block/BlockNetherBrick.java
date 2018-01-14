package cn.nukkit.server.block;

import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemTool;
import cn.nukkit.server.util.BlockColor;

/**
 * Created on 2015/12/7 by xtypr.
 * Package cn.nukkit.server.block in project Nukkit .
 */
public class BlockNetherBrick extends BlockSolid {

    public BlockNetherBrick() {
        this(0);
    }

    public BlockNetherBrick(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Nether Bricks";
    }

    @Override
    public int getId() {
        return NETHER_BRICKS;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 10;
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
