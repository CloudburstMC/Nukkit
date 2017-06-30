package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockMossStone extends BlockSolid {

    public BlockMossStone() {
        this(0);
    }

    public BlockMossStone(int meta) {
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
    public double getResistance() {
        return 10;
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
