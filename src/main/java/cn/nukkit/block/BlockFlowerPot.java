package cn.nukkit.block;

import cn.nukkit.item.Item;

/**
 * @author Nukkit Project Team
 */
public class BlockFlowerPot extends BlockFlowable {

    public BlockFlowerPot(int meta) {
        super(meta);
    }

    public BlockFlowerPot() {
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
