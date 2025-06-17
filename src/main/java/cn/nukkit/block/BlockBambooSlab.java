package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.utils.BlockColor;

public class BlockBambooSlab extends BlockSlabWood {

    public BlockBambooSlab() {
        this(0);
    }

    public BlockBambooSlab(int meta) {
        super(meta, BAMBOO_DOUBLE_SLAB);
    }

    @Override
    public String getName() {
        return "Bamboo Slab";
    }

    @Override
    public int getId() {
        return BAMBOO_SLAB;
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
