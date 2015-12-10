package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;

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
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        Block down = this.getSide(Vector3.SIDE_DOWN);
        if (down.getId() == Block.GRASS) {
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
    public int[][] getDrops(Item item) {
        //todo

        return new int[0][];
    }
}
