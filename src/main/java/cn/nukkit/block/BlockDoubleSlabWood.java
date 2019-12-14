package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockDoubleSlabWood extends BlockSolid {

    public BlockDoubleSlabWood(int id, int meta) {
        super(id, meta);
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
    public String getName() {
        String[] names = new String[]{
                "Oak",
                "Spruce",
                "Birch",
                "Jungle",
                "Acacia",
                "Dark Oak",
                "",
                ""
        };
        return "Double " + names[this.getDamage() & 0x07] + " Slab";
    }

    @Override
    public Item toItem() {
        return Item.get(WOODEN_SLAB, this.getDamage() & 0x07);
    }

    public Item[] getDrops(Item item) {
        return new Item[]{
                Item.get(Item.WOOD_SLAB, this.getDamage() & 0x07, 2)
        };
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }
}
