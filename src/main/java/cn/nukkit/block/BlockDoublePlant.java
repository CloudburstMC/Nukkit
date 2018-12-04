package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemSeedsWheat;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import java.util.Random;

/**
 * Created on 2015/11/23 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
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
        return this.getDamage() == 2 || this.getDamage() == 3;
    }

    @Override
    public String getName() {
        return NAMES[this.getDamage() > 5 ? 0 : this.getDamage()];
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if ((this.getDamage() & 0x08) == 8) {
                // Top
                if (!(this.down().getId() == DOUBLE_PLANT)) {
                    this.getLevel().setBlock(this, new BlockAir(), false, true);
                    return Level.BLOCK_UPDATE_NORMAL;
                }
            } else {
                // Bottom
                if (this.down().isTransparent() || !(this.up().getId() == DOUBLE_PLANT)) {
                    this.getLevel().useBreakOn(this);
                    return Level.BLOCK_UPDATE_NORMAL;
                }
            }
        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        Block down = down();
        Block up = up();

        if (up.getId() == 0 && (down.getId() == GRASS || down.getId() == DIRT)) {
            this.getLevel().setBlock(block, this, true, false); // If we update the bottom half, it will drop the item because there isn't a flower block above
            this.getLevel().setBlock(up, new BlockDoublePlant(getDamage() ^ 0x08), true, true);
            return true;
        }

        return false;
    }

    @Override
    public boolean onBreak(Item item) {
        Block down = down();

        if ((this.getDamage() & 0x08) == 0x08) { // Top half
            this.getLevel().useBreakOn(down);
        } else {
            this.getLevel().setBlock(this, new BlockAir(), true, true);
        }

        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        if ((this.getDamage() & 0x08) != 0x08) {
            switch (this.getDamage() & 0x07) {
                case 2:
                case 3:
                    boolean dropSeeds = new Random().nextInt(10) == 0;
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
                        return new Item[0];
                    }
            }

            return new Item[]{toItem()};
        }

        return new Item[0];
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }
}