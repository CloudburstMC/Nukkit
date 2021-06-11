package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.value.StoneSlab3Type;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

@PowerNukkitDifference(info = "Extends BlockDoubleSlabBase instead of BlockDoubleSlabStone only in PowerNukkit")
public class BlockDoubleSlabStone3 extends BlockDoubleSlabBase {
    public static final int END_STONE_BRICKS = 0;
    public static final int SMOOTH_RED_SANDSTONE = 1;
    public static final int POLISHED_ANDESITE = 2;
    public static final int ANDESITE = 3;
    public static final int DIORITE = 4;
    public static final int POLISHED_DIORITE = 5;
    public static final int GRANITE = 6;
    public static final int POLISHED_GRANITE = 7;

    public BlockDoubleSlabStone3() {
        this(0);
    }

    public BlockDoubleSlabStone3(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DOUBLE_STONE_SLAB3;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return BlockSlabStone3.PROPERTIES;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public StoneSlab3Type getSlabType() {
        return getPropertyValue(StoneSlab3Type.PROPERTY);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setSlabType(StoneSlab3Type type) {
        setPropertyValue(StoneSlab3Type.PROPERTY, type);
    }

    @Override
    public String getSlabName() {
        return getSlabType().getEnglishName();
    }

    @Override
    public double getResistance() {
        return getToolType() > ItemTool.TIER_WOODEN ? 30 : 15;
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
        return STONE_SLAB3;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public BlockColor getColor() {
        return getSlabType().getColor();
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
