package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.value.DoublePlantType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.blockproperty.CommonBlockProperties.UPPER_BLOCK;

/**
 * @author xtypr
 * @since 2015/11/23
 */
public class BlockDoublePlant extends BlockFlowable {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final ArrayBlockProperty<DoublePlantType> DOUBLE_PLANT_TYPE = new ArrayBlockProperty<>(
            "double_plant_type", true, DoublePlantType.class
    );

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(DOUBLE_PLANT_TYPE, UPPER_BLOCK);

    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", replaceWith = "DoublePlantType.SUNFLOWER",
        reason = "Magic values may change in future without backward compatibility.")
    public static final int SUNFLOWER = 0;
    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", replaceWith = "DoublePlantType.LILAC",
            reason = "Magic values may change in future without backward compatibility.")
    public static final int LILAC = 1;
    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", replaceWith = "DoublePlantType.TALL_GRASS",
            reason = "Magic values may change in future without backward compatibility.")
    public static final int TALL_GRASS = 2;
    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", replaceWith = "DoublePlantType.LARGE_FERN",
            reason = "Magic values may change in future without backward compatibility.")
    public static final int LARGE_FERN = 3;
    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", replaceWith = "DoublePlantType.ROSE_BUSH",
            reason = "Magic values may change in future without backward compatibility.")
    public static final int ROSE_BUSH = 4;
    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", replaceWith = "DoublePlantType.PEONY",
            reason = "Magic values may change in future without backward compatibility.")
    public static final int PEONY = 5;
    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", replaceWith = "CommonBlockProperties.UPPER_BLOCK",
            reason = "Magic values may change in future without backward compatibility.")
    public static final int TOP_HALF_BITMASK = 0x8;

    public BlockDoublePlant() {
        this(0);
    }

    public BlockDoublePlant(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DOUBLE_PLANT;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public DoublePlantType getDoublePlantType() {
        return getPropertyValue(DOUBLE_PLANT_TYPE);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setDoublePlantType(@Nonnull DoublePlantType type) {
        setPropertyValue(DOUBLE_PLANT_TYPE, type);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isTopHalf() {
        return getBooleanValue(UPPER_BLOCK);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setTopHalf(boolean topHalf) {
        setBooleanValue(UPPER_BLOCK, topHalf);
    }

    @Override
    public boolean canBeReplaced() {
        return getDoublePlantType().isReplaceable();
    }

    @Override
    public String getName() {
        return getDoublePlantType().getEnglishName();
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Bottom part will break if the supporting block is invalid on normal update")
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (isTopHalf()) {
                // Top
                if (this.down().getId() != DOUBLE_PLANT) {
                    this.getLevel().setBlock(this, Block.get(BlockID.AIR), false, true);
                    return Level.BLOCK_UPDATE_NORMAL;
                }
            } else {
                // Bottom
                if (this.up().getId() != DOUBLE_PLANT || !isSupportValid(down())) {
                    this.getLevel().useBreakOn(this);
                    return Level.BLOCK_UPDATE_NORMAL;
                }
            }
        }
        return 0;
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        Block up = up();

        if (up.getId() == AIR && isSupportValid(down())) {
            setTopHalf(false);
            this.getLevel().setBlock(block, this, true, false); // If we update the bottom half, it will drop the item because there isn't a flower block above

            setTopHalf(true);
            this.getLevel().setBlock(up, this, true, true);
            this.getLevel().updateAround(this);
            return true;
        }

        return false;
    }

    private boolean isSupportValid(Block support) {
        switch (support.getId()) {
            case GRASS:
            case DIRT:
            case PODZOL:
            case FARMLAND:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onBreak(Item item) {
        Block down = down();

        if (isTopHalf()) { // Top half
            this.getLevel().useBreakOn(down);
        } else {
            this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true);
        }

        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (isTopHalf()){
            return Item.EMPTY_ARRAY;
        }

        switch (getDoublePlantType()) {
            case GRASS:
            case FERN:
                boolean dropSeeds = ThreadLocalRandom.current().nextInt(10) == 0;
                if (item.isShears()) {
                    //todo enchantment
                    if (dropSeeds) {
                        return new Item[]{
                                Item.get(ItemID.WHEAT_SEEDS),
                                toItem()
                        };
                    } else {
                        return new Item[]{
                                toItem()
                        };
                    }
                }

                if (dropSeeds) {
                    return new Item[]{
                            Item.get(ItemID.WHEAT_SEEDS)
                    };
                } else {
                    return Item.EMPTY_ARRAY;
                }
        }

        return new Item[]{toItem()};
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {
        if (item.isFertilizer()) { //Bone meal
            switch (getDoublePlantType()) {
                case SUNFLOWER:
                case SYRINGA:
                case ROSE:
                case PAEONIA:
                    if (player != null && (player.gamemode & 0x01) == 0) {
                        item.count--;
                    }
                    this.level.addParticle(new BoneMealParticle(this));
                    this.level.dropItem(this, this.toItem());
            }

            return true;
        }

        return false;
    }
}
