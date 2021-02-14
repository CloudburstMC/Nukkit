package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemSeedsWheat;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author xtypr
 * @since 2015/11/23
 */
public class BlockDoublePlant extends BlockFlowable {
    public static final int SUNFLOWER = 0;
    public static final int LILAC = 1;
    public static final int TALL_GRASS = 2;
    public static final int LARGE_FERN = 3;
    public static final int ROSE_BUSH = 4;
    public static final int PEONY = 5;
    public static final int TOP_HALF_BITMASK = 0x8;

    private static final String[] NAMES = new String[]{
            "Sunflower",
            "Lilac",
            "Double Tallgrass",
            "Large Fern",
            "Rose Bush",
            "Peony"
    };

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

    @Override
    public boolean canBeReplaced() {
        int damage = this.getDamage() & 0x7;
        return damage == TALL_GRASS || damage == LARGE_FERN;
    }

    @Override
    public String getName() {
        return NAMES[this.getDamage() > 5 ? 0 : this.getDamage()];
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Bottom part will break if the supporting block is invalid on normal update")
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if ((this.getDamage() & TOP_HALF_BITMASK) == TOP_HALF_BITMASK) {
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
            this.getLevel().setBlock(block, this, true, false); // If we update the bottom half, it will drop the item because there isn't a flower block above
            this.getLevel().setBlock(up, Block.get(BlockID.DOUBLE_PLANT, getDamage() ^ TOP_HALF_BITMASK), true, true);
            return true;
        }

        return false;
    }
    
    private boolean isSupportValid(Block support) {
        switch (support.getId()) {
            case GRASS:
            case DIRT:
                return true;
            default:
                return false;
        }
    } 

    @Override
    public boolean onBreak(Item item) {
        Block down = down();

        if ((this.getDamage() & TOP_HALF_BITMASK) == TOP_HALF_BITMASK) { // Top half
            this.getLevel().useBreakOn(down);
        } else {
            this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true);
        }

        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        if ((this.getDamage() & TOP_HALF_BITMASK) != TOP_HALF_BITMASK) {
            switch (this.getDamage() & 0x07) {
                case TALL_GRASS:
                case LARGE_FERN:
                    boolean dropSeeds = ThreadLocalRandom.current().nextInt(10) == 0;
                    if (item.isShears()) {
                        //todo enchantment
                        if (dropSeeds) {
                            return new Item[]{
                                    new ItemSeedsWheat(0, 1),
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
                                new ItemSeedsWheat()
                        };
                    } else {
                        return Item.EMPTY_ARRAY;
                    }
            }

            return new Item[]{toItem()};
        }

        return Item.EMPTY_ARRAY;
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
            switch (this.getDamage() & 0x07) {
                case SUNFLOWER:
                case LILAC:
                case ROSE_BUSH:
                case PEONY:
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
