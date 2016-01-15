package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;

import java.util.Random;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class TallGrass extends Flowable {

    public TallGrass() {
        this(1);
    }

    public TallGrass(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return TALL_GRASS;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "Dead Shrub",
                "Tall Grass",
                "Fern",
                ""
        };
        return names[this.meta & 0x03];
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        Block down = this.getSide(Vector3.SIDE_DOWN);
        if (down.getId() == Block.GRASS || down.getId() == Block.DIRT || down.getId() == Block.PODZOL) {
            this.getLevel().setBlock(block, this, true);
            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.getSide(0).isTransparent()) { //Replace with common break method
                this.getLevel().setBlock(this, new Air(), false, false);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public boolean onActivate(Item item) {
        return this.onActivate(item, null);
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.getId() == Item.DYE && item.getDamage() == 0x0F) {
            if (this.getLevel().getBlock(new Vector3(this.x, this.y + 1, this.z)).getId() == AIR) {
                if (this.meta == 0) {
                    this.getLevel().setBlock(this, Block.get(DOUBLE_PLANT, 2), true, true);
                    this.getLevel().setBlock(new Vector3(this.x, this.y + 1, this.z), Block.get(DOUBLE_PLANT, 10), true, true);

                    item.count--;
                    return true;
                } else if (this.meta == 1) {
                    this.getLevel().setBlock(this, Block.get(DOUBLE_PLANT, 3), true, true);
                    this.getLevel().setBlock(new Vector3(this.x, this.y + 1, this.z), Block.get(DOUBLE_PLANT, 11), true, true);

                    item.count--;
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public int[][] getDrops(Item item) {
        boolean dropSeeds = new Random().nextInt(10) == 0;
        if (item.isShears()) {
            //todo enchantment
            if (dropSeeds) {
                return new int[][]{
                        {Item.SEEDS, 0, 1},
                        {Item.TALL_GRASS, this.meta, 1}
                };
            } else {
                return new int[][]{
                        {Item.TALL_GRASS, this.meta, 1}
                };
            }
        }

        if (dropSeeds) {
            return new int[][]{
                    {Item.SEEDS, 0, 1},
            };
        } else {
            return new int[0][];
        }
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_SHEARS;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }
}
