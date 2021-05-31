package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public abstract class BlockDoubleSlabBase extends BlockSolidMeta {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockDoubleSlabBase(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Double "+getSlabName()+" Slab";
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public abstract String getSlabName();
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public abstract int getSingleSlabId();

    @Override
    public Item toItem() {
        return getCurrentState().forItem().withBlockId(getSingleSlabId()).asItemBlock();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
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
            return Item.EMPTY_ARRAY;
        }
    }
}
