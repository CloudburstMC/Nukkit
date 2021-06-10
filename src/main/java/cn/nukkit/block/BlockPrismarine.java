package cn.nukkit.block;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import cn.nukkit.blockproperty.value.PrismarineBlockType;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;


public class BlockPrismarine extends BlockSolidMeta {
    @PowerNukkitOnly
    @Since("FUTURE")
    public static final BlockProperty<PrismarineBlockType> PRISMARINE_BLOCK_TYPE = new ArrayBlockProperty<>("prismarine_block_type", true, PrismarineBlockType.class);

    @PowerNukkitOnly
    @Since("FUTURE")
    public static final BlockProperties PROPERTIES = new BlockProperties(PRISMARINE_BLOCK_TYPE);

    @Deprecated
    @DeprecationDetails(since = "FUTURE", replaceWith = "getPrismarineBlockType()", reason = "Use the BlockProperty API instead")
    public static final int NORMAL = 0;

    @DeprecationDetails(since = "FUTURE", replaceWith = "getPrismarineBlockType()", reason = "Use the BlockProperty API instead")
    public static final int DARK = 1;

    @DeprecationDetails(since = "FUTURE", replaceWith = "getPrismarineBlockType()", reason = "Use the BlockProperty API instead")
    public static final int BRICKS = 2;

    public BlockPrismarine() {
        this(0);
    }

    public BlockPrismarine(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PRISMARINE;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return getPrismarineBlockType().getEnglishName();
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public void setPrismarineBlockType(PrismarineBlockType prismarineBlockType) {
        setPropertyValue(PRISMARINE_BLOCK_TYPE, prismarineBlockType);
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public PrismarineBlockType getPrismarineBlockType() {
        return getPropertyValue(PRISMARINE_BLOCK_TYPE);
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return getPrismarineBlockType().getColor();
    }
}
