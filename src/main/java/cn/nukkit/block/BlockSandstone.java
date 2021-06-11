package cn.nukkit.block;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import cn.nukkit.blockproperty.value.SandStoneType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockSandstone extends BlockSolidMeta {
    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperty<SandStoneType> SAND_STONE_TYPE = new ArrayBlockProperty<>("sand_stone_type", true, SandStoneType.class);

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(SAND_STONE_TYPE);

    @Deprecated
    @DeprecationDetails(since = "1.5.0.0-PN", replaceWith = "getSandstoneBlockType()", reason = "Use the BlockProperty API instead")
    public static final int NORMAL = 0;

    @Deprecated
    @DeprecationDetails(since = "1.5.0.0-PN", replaceWith = "getSandstoneBlockType()", reason = "Use the BlockProperty API instead")
    public static final int CHISELED = 1;

    @Deprecated
    @DeprecationDetails(since = "1.5.0.0-PN", replaceWith = "getSandstoneBlockType()", reason = "Use the BlockProperty API instead")
    public static final int SMOOTH = 2;

    public BlockSandstone() {
        this(0);
    }

    public BlockSandstone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SANDSTONE;
    }

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public void setSandstoneType(SandStoneType sandStoneType) {
        setPropertyValue(SAND_STONE_TYPE, sandStoneType);
    }

    public SandStoneType getSandstoneType() {
        return getPropertyValue(SAND_STONE_TYPE);
    }

    @Override
    public double getHardness() {
        return SandStoneType.SMOOTH.equals(getSandstoneType()) ? 2 : 0.8;
    }

    @Override
    public double getResistance() {
        return SandStoneType.SMOOTH.equals(getSandstoneType()) ? 6 : 0.8;
    }

    @Override
    public String getName() {
        return getSandstoneType().getEnglishName();
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
