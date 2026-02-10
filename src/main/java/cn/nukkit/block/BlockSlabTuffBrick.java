package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockSlabTuffBrick extends BlockSlab {

    public BlockSlabTuffBrick() {
        this(0);
    }

    public BlockSlabTuffBrick(int meta) {
        super(meta, TUFF_BRICK_DOUBLE_SLAB);
    }

    @Override
    public int getId() {
        return TUFF_BRICK_SLAB;
    }

    @Override
    public String getSlabName() {
        return "Tuff Brick Slab";
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                this.toItem()
        };
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }

    @Override
    public boolean hasTopBit() {
        return (this.getDamage() & 0x01) == 1;
    }

    @Override
    public void setTopBit(boolean topBit) {
        this.setDamage(topBit ? 1 : 0);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRAY_TERRACOTA_BLOCK_COLOR;
    }
}
