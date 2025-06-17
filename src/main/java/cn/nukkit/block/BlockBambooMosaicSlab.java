package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.utils.BlockColor;

public class BlockBambooMosaicSlab extends BlockSlabWood {

    public BlockBambooMosaicSlab() {
        this(0);
    }

    public BlockBambooMosaicSlab(int meta) {
        super(meta, BAMBOO_MOSAIC_DOUBLE_SLAB);
    }

    @Override
    public String getName() {
        return "Bamboo Mosaic Slab";
    }

    @Override
    public int getId() {
        return BAMBOO_MOSAIC_SLAB;
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
        return BlockColor.YELLOW_BLOCK_COLOR;
    }
}
