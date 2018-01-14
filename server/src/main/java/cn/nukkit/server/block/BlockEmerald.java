package cn.nukkit.server.block;

import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemTool;
import cn.nukkit.server.util.BlockColor;

/**
 * Created on 2015/12/1 by xtypr.
 * Package cn.nukkit.server.block in project Nukkit .
 */
public class BlockEmerald extends BlockSolid {

    public BlockEmerald() {
        this(0);
    }

    public BlockEmerald(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Emerald BlockType";
    }

    @Override
    public int getId() {
        return EMERALD_BLOCK;
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
        return BlockColor.EMERALD_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
