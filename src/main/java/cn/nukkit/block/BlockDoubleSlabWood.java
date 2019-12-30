package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.WOODEN_SLAB;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockDoubleSlabWood extends BlockSolid {

    public BlockDoubleSlabWood(Identifier id) {
        super(id);
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
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public Item toItem() {
        return Item.get(WOODEN_SLAB, this.getDamage() & 0x07);
    }

    public Item[] getDrops(Item item) {
        return new Item[]{
                Item.get(WOODEN_SLAB, this.getDamage() & 0x07, 2)
        };
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }
}
