package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.value.StoneSlab2Type;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

/**
 * @author CreeperFace
 * @since 26. 11. 2016
 */
@PowerNukkitDifference(info = "Extends BlockDoubleSlabBase only in PowerNukkit")
public class BlockDoubleSlabRedSandstone extends BlockDoubleSlabBase {

    public BlockDoubleSlabRedSandstone() {
        this(0);
    }

    public BlockDoubleSlabRedSandstone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DOUBLE_RED_SANDSTONE_SLAB;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return BlockSlabRedSandstone.PROPERTIES;
    }

    public StoneSlab2Type getSlabType() {
        return getPropertyValue(StoneSlab2Type.PROPERTY);
    }

    public void setSlabType(StoneSlab2Type type) {
        setPropertyValue(StoneSlab2Type.PROPERTY, type);
    }

    @Override
    public String getSlabName() {
        return getSlabType().getEnglishName();
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getSingleSlabId() {
        return RED_SANDSTONE_SLAB;
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
        return getSlabType().getColor();
    }
}
