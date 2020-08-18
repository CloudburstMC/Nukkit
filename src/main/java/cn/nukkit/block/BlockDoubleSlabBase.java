package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

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
    
    //TODO Adjust or remove this when merging https://github.com/PowerNukkit/PowerNukkit/pull/370
    protected boolean isCorrectTool(Item item) {
        return item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN;
    }

    //TODO Adjust or remove this when merging https://github.com/PowerNukkit/PowerNukkit/pull/370
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
