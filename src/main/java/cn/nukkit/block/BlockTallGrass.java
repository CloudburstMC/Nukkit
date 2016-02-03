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
public class BlockTallGrass extends BlockFlowable {

    public BlockTallGrass() {
        this(1);
    }

    public BlockTallGrass(int meta) {
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
    public boolean canBeActivated() {
        return true;
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
            if (this.getSide(0).isTransparent()) {
                this.getLevel().useBreakOn(this);
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
        //todo bonemeal

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
