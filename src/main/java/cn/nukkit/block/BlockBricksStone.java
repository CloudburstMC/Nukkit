package cn.nukkit.block;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import cn.nukkit.blockproperty.value.StoneBrickType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

import javax.annotation.Nonnull;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockBricksStone extends BlockSolidMeta {
    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperty<StoneBrickType> STONE_BRICK_TYPE = new ArrayBlockProperty<>("stone_brick_type", true, StoneBrickType.class);

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(STONE_BRICK_TYPE);

    @Deprecated
    @DeprecationDetails(since = "1.5.0.0-PN", replaceWith = "getStoneBrickType()", reason = "Use the BlockProperty API instead")
    public static final int NORMAL = 0;

    @Deprecated
    @DeprecationDetails(since = "1.5.0.0-PN", replaceWith = "getStoneBrickType()", reason = "Use the BlockProperty API instead")
    public static final int MOSSY = 1;

    @Deprecated
    @DeprecationDetails(since = "1.5.0.0-PN", replaceWith = "getStoneBrickType()", reason = "Use the BlockProperty API instead")
    public static final int CRACKED = 2;

    @Deprecated
    @DeprecationDetails(since = "1.5.0.0-PN", replaceWith = "getStoneBrickType()", reason = "Use the BlockProperty API instead")
    public static final int CHISELED = 3;

    public BlockBricksStone() {
        this(0);
    }

    public BlockBricksStone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STONE_BRICKS;
    }

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
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

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public void setBrickStoneType(StoneBrickType stoneBrickType) {
        setPropertyValue(STONE_BRICK_TYPE, stoneBrickType);
    }

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public StoneBrickType getStoneBrickType() {
        return getPropertyValue(STONE_BRICK_TYPE);
    }

    @Override
    public String getName() {
        return getStoneBrickType().getEnglishName();
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[] {
                    toItem()
            };
        } else {
            return Item.EMPTY_ARRAY;
        }
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
