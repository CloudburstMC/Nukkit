package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockSlabBlackstone extends BlockSlab {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockSlabBlackstone() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockSlabBlackstone(int meta) {
        super(meta, BLACKSTONE_DOUBLE_SLAB);
    }

    @Override
    public int getId() {
        return BLACKSTONE_SLAB;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return SIMPLE_SLAB_PROPERTIES;
    }

    @Override
    public String getSlabName() {
        return "Blackstone Slab";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId() == getId();
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem()
            };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BLACK_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
