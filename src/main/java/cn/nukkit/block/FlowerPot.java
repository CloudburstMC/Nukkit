package cn.nukkit.block;

import cn.nukkit.item.Item;

/**
 * @author Nukkit Project Team
 */
public class FlowerPot extends Flowable {

    public FlowerPot(int meta) {
        super(meta);
    }

    public FlowerPot() {
        this(0);
    }

    @Override
    public String getName() {
        return "Flower Pot";
    }

    @Override
    public int getId() {
        return FLOWER_POT_BLOCK;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{
                {Item.FLOWER_POT, 0, 1}
        };
    }

}
