package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

/**
 * Created on 2015/11/24 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class HayBale extends Solid{
    public HayBale() {
        this(0);
    }

    public HayBale(int meta) {
        super(HAY_BALE, meta);
    }

    @Override
    public String getName() {
        return "Hay Bale";
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        int[] faces = new int[]{
                0,
                0,
                0b1000,
                0b1000,
                0b0100,
                0b0100,
        };
        this.meta = (this.meta & 0x03) | faces[face];
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{
                {this.id, 0, 1}
        };
    }
}
