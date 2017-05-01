package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import java.util.Random;

/**
 * Created on 2015/11/23 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockDoublePlant extends BlockFlowable {

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
        return this.meta == 2 || this.meta == 3;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "Sunflower",
                "Lilac",
                "Double Tallgrass",
                "Large Fern",
                "Rose Bush",
                "Peony"
        };
        return names[this.meta & 0x07];
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if ((this.meta & 0x08) == 8) {
                // Top
                if (!(this.down().getId() == DOUBLE_PLANT)) {
                    this.getLevel().setBlock(this, new BlockAir(), true, true);
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
            this.getLevel().setBlock(up, new BlockDoublePlant(meta ^ 0x08), true, true);
            return true;
        }

        return false;
    }

    @Override
    public boolean onBreak(Item item) {
        Block down = down();

        if ((this.meta & 0x08) == 0x08) { // Top half
            this.getLevel().useBreakOn(down);
        } else {
            this.getLevel().setBlock(this, new BlockAir(), true, true);
        }

        return true;
    }

    @Override
    public int[][] getDrops(Item item) {
        if ((this.meta & 0x08) != 0x08) {
            switch (this.meta & 0x07) {
                case 2:
                case 3:
                    boolean dropSeeds = new Random().nextInt(10) == 0;
                    if (item.isShears()) {
                        //todo enchantment
                        if (dropSeeds) {
                            return new int[][]{
                                    {Item.SEEDS, 0, 1},
                                    {DOUBLE_PLANT, this.meta, 1}
                            };
                        } else {
                            return new int[][]{
                                    {DOUBLE_PLANT, this.meta, 1}
                            };
                        }
                    }

                    if (dropSeeds) {
                        return new int[][]{
                                {Item.SEEDS, 0, 1},
                        };
                    } else {
                        return new int[0][0];
                    }
            }

            return new int[][]{{DOUBLE_PLANT, this.meta, 1}};
        }

        return new int[0][0];
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }
}