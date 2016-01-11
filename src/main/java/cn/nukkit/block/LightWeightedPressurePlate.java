package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * @author Nukkit Project Team
 */
public class LightWeightedPressurePlate extends Transparent {

    public LightWeightedPressurePlate(int meta) {
        super(meta);
    }

    public LightWeightedPressurePlate() {
        this(0);
    }

    @Override
    public String getName() {
        return "Light Weighted Pressure Plate";
    }

    @Override
    public int getId() {
        return LIGHT_WEIGHTED_PRESSURE_PLATE;
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
        if(item.isPickaxe()){
            return new int[][]{
                {Item.LIGHT_WEIGHTED_PRESSURE_PLATE, 0, 1}
            };
        }
        return new int[][]{{}};
    }

}
