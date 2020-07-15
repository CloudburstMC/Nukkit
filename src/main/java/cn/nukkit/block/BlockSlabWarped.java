package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;

public class BlockSlabWarped extends BlockSlab {

    public BlockSlabWarped() {
        this(0);
    }

    public BlockSlabWarped(int meta) {
        super(meta, WARPED_DOUBLE_SLAB);
    }

    @Override
    public String getName() {
        return "Warped Slab";
    }

    @Override
    public int getId() {
        return WARPED_SLAB;
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
        return new ItemBlock(this);
    }
    
    @Override
    public int getBurnChance() {
        return 0;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }

    /*@Override
    public BlockColor getColor() {
        return BlockColor.BLACK_BLOCK_COLOR;
    }*/
}
