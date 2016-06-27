package cn.nukkit.block;

import cn.nukkit.item.Item;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockGlass extends BlockTransparent {

    public BlockGlass() {
        this(0);
    }

    public BlockGlass(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return GLASS;
    }

    @Override
    public String getName() {
        return "Glass";
    }

    @Override
    public double getResistance() {
        return 1.5;
    }

    @Override
    public double getHardness() {
        return 0.3;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[0][0];
    }
}
