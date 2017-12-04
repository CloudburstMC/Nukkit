package cn.nukkit.server.block;

import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemTool;

/**
 * Created on 2015/12/1 by xtypr.
 * Package cn.nukkit.server.block in project Nukkit .
 */
public class BlockEndStone extends BlockSolid {

    public BlockEndStone() {
        this(0);
    }

    public BlockEndStone(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "End Stone";
    }

    @Override
    public int getId() {
        return END_STONE;
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 45;
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
    public boolean canHarvestWithHand() {
        return false;
    }
}
