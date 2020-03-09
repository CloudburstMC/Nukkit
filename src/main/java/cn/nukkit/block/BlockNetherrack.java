package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

/**
 * Created on 2015/12/26 by Pub4Game.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockNetherrack extends BlockSolid {

    public BlockNetherrack(Identifier id) {
        super(id);
    }

    @Override
    public float getResistance() {
        return 2;
    }

    @Override
    public float getHardness() {
        return 0.4f;
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
        return BlockColor.NETHERRACK_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

}
