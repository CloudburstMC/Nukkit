package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.value.StoneSlab2Type;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

/**
 * @author CreeperFace
 * @since 26. 11. 2016
 */
public class BlockSlabRedSandstone extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(
            StoneSlab2Type.PROPERTY,
            TOP_SLOT_PROPERTY
    );

    public static final int RED_SANDSTONE = 0;
    public static final int PURPUR = 1;
    public static final int PRISMARINE = 2;
    public static final int PRISMARINE_BRICKS = 3;
    public static final int DARK_PRISMARINE = 4;
    public static final int MOSSY_COBBLESTONE = 5;
    public static final int SMOOTH_SANDSTONE = 6;
    public static final int RED_NETHER_BRICK = 7;

    public BlockSlabRedSandstone() {
        this(0);
    }

    public BlockSlabRedSandstone(int meta) {
        super(meta, DOUBLE_RED_SANDSTONE_SLAB);
    }

    @Override
    public int getId() {
        return RED_SANDSTONE_SLAB;
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

    public StoneSlab2Type getSlabType() {
        return getPropertyValue(StoneSlab2Type.PROPERTY);
    }
    
    public void setSlabType(StoneSlab2Type type) {
        setPropertyValue(StoneSlab2Type.PROPERTY, type);
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId() == getId() && getSlabType().equals(slab.getPropertyValue(StoneSlab2Type.PROPERTY));
    }

    @Override
    public BlockColor getColor() {
        return getSlabType().getColor();
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
