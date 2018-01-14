package cn.nukkit.server.block;

import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemBlock;
import cn.nukkit.server.item.ItemTool;
import cn.nukkit.server.util.BlockColor;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.server.block in project Nukkit .
 */
public class BlockSlabWood extends BlockSlab {

    public BlockSlabWood() {
        this(0);
    }

    public BlockSlabWood(int meta) {
        super(meta, DOUBLE_WOODEN_SLAB);
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
        return (((this.meta & 0x08) == 0x08) ? "Upper " : "") + names[this.meta & 0x07] + " Wooden Slab";
    }

    @Override
    public int getId() {
        return WOOD_SLAB;
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
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                toItem()
        };
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, this.meta & 0x07);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }
}
