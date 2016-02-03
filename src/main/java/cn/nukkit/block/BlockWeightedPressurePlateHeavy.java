package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * @author Nukkit Project Team
 */
public class BlockWeightedPressurePlateHeavy extends BlockWeightedPressurePlate {

    public BlockWeightedPressurePlateHeavy(int meta) {
        super(meta);
    }

    public BlockWeightedPressurePlateHeavy() {
        this(0);
    }

    @Override
    public String getName() {
        return "Heavy Weighted Pressure Plate";
    }

    @Override
    public int getId() {
        return Block.HEAVY_WEIGHTED_PRESSURE_PLATE;
    }

    @Override
    public double getHardness() {
        return 0.5D;
    }

    @Override
    public double getResistance() {
        return 2.5D;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe()) {
            return new int[][]{
                    {Item.HEAVY_WEIGHTED_PRESSURE_PLATE, 0, 1}
            };
        }
        return new int[][]{{}};
    }

}
