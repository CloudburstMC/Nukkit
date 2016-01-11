package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.redstone.Redstone;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class RedstoneTorch extends Torch {

    public RedstoneTorch() {
        this(0);
    }

    public RedstoneTorch(int meta) {
        super(meta);
        this.setPowerSource(true);
        this.setPowerLevel(Redstone.POWER_STRONGEST);
    }

    @Override
    public String getName() {
        return "Redstone Torch";
    }

    @Override
    public int getId() {
        return REDSTONE_TORCH;
    }

    @Override
    public int getLightLevel() {
        return 7;
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        Block below = this.getSide(0);

        if (!target.isTransparent() && face != 0) {
            int[] faces = new int[]{
                    0, //0, nerver used
                    5, //1
                    4, //2
                    3, //3
                    2, //4
                    1, //5
            };
            this.meta = faces[face];
            this.getLevel().setBlock(block, this, true, true);
            Redstone.active(this);

            return true;
        } else if (!below.isTransparent() || below instanceof Fence || below.getId() == COBBLE_WALL) {
            this.meta = 0;
            this.getLevel().setBlock(block, this, true, true);
            Redstone.active(this);

            return true;
        }
        return false;
    }


    @Override
    public boolean onBreak(Item item) {
        int level = this.getPowerLevel();
        this.getLevel().setBlock(this, new Air(), true, false);
        Redstone.deactive(this, level);
        return true;
    }

}
