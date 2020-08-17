package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.value.StoneSlab3Type;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

public class BlockSlabStone3 extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(
            StoneSlab3Type.PROPERTY,
            TOP_SLOT_PROPERTY
    );
    
    public static final int END_STONE_BRICKS = 0;
    public static final int SMOOTH_RED_SANDSTONE = 1;
    public static final int POLISHED_ANDESITE = 2;
    public static final int ANDESITE = 3;
    public static final int DIORITE = 4;
    public static final int POLISHED_DIORITE = 5;
    public static final int GRANITE = 6;
    public static final int POLISHED_GRANITE = 7;

    public BlockSlabStone3() {
        this(0);
    }

    public BlockSlabStone3(int meta) {
        super(meta, DOUBLE_STONE_SLAB3);
    }

    @Override
    public int getId() {
        return STONE_SLAB3;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getSlabName() {
        return getSlabType().getEnglishName();
    }

    public StoneSlab3Type getSlabType() {
        return getPropertyValue(StoneSlab3Type.PROPERTY);
    }
    
    public void setSlabType(StoneSlab3Type type) {
        setPropertyValue(StoneSlab3Type.PROPERTY, type);
    }


    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId() == getId() && getSlabType().equals(slab.getPropertyValue(StoneSlab3Type.PROPERTY));
    }


    @Override
    public BlockColor getColor() {
        return getSlabType().getColor();
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem()
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
