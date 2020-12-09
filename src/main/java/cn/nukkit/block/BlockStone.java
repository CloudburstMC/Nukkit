package cn.nukkit.block;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import cn.nukkit.blockproperty.value.StoneType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockStone extends BlockSolidMeta {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperty<StoneType> STONE_TYPE = new ArrayBlockProperty<>("stone_type", true, StoneType.class);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(STONE_TYPE);
    
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", replaceWith = "getStoneType()", reason = "Use the BlockProperty API instead")
    public static final int NORMAL = 0;

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", replaceWith = "getStoneType()", reason = "Use the BlockProperty API instead")
    public static final int GRANITE = 1;

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", replaceWith = "getStoneType()", reason = "Use the BlockProperty API instead")
    public static final int POLISHED_GRANITE = 2;

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", replaceWith = "getStoneType()", reason = "Use the BlockProperty API instead")
    public static final int DIORITE = 3;

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", replaceWith = "getStoneType()", reason = "Use the BlockProperty API instead")
    public static final int POLISHED_DIORITE = 4;

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", replaceWith = "getStoneType()", reason = "Use the BlockProperty API instead")
    public static final int ANDESITE = 5;

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", replaceWith = "getStoneType()", reason = "Use the BlockProperty API instead")
    public static final int POLISHED_ANDESITE = 6;

    public BlockStone() {
        this(0);
    }

    public BlockStone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STONE;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
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
        return 10;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public StoneType getStoneType() {
        return getPropertyValue(STONE_TYPE);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setStoneType(StoneType stoneType) {
        setPropertyValue(STONE_TYPE, stoneType);
    }

    @Override
    public String getName() {
        return getStoneType().getEnglishName();
    }

    @Override
    public BlockColor getColor() {
        return getStoneType().getColor();
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= getToolTier()) {
            return new Item[]{
                    StoneType.STONE.equals(getStoneType())
                            ? Item.getBlock(BlockID.COBBLESTONE)
                            : toItem()
            };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }
}
