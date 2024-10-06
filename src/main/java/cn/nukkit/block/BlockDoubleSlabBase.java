package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;

public abstract class BlockDoubleSlabBase extends BlockSolidMeta {

    public BlockDoubleSlabBase() {
        this(0);
    }

    public BlockDoubleSlabBase(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Double " + this.getSlabName() + " Slab";
    }

    public abstract String getSlabName();

    public abstract int getSingleSlabId();

    public abstract int getItemDamage();

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getSingleSlabId(), this.getItemDamage()), this.getItemDamage(), 1);
    }

    protected boolean isCorrectTool(Item item) {
        return canHarvestWithHand() || canHarvest(item);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (isCorrectTool(item)) {
            Item slab = toItem();
            slab.setCount(2);
            return new Item[]{ slab };
        } else {
            return new Item[0];
        }
    }
}
