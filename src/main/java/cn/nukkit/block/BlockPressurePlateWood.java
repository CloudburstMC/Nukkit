package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * @author Nukkit Project Team
 */
public class BlockPressurePlateWood extends BlockPressurePlate {

    public BlockPressurePlateWood(int meta) {
        super(meta);
    }

    public BlockPressurePlateWood() {
        this(0);
    }

    @Override
    public String getName() {
        return "Wooden Pressure Plate";
    }

    @Override
    public int getId() {
        return WOODEN_PRESSURE_PLATE;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
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
    public int[][] getDrops(Item item) {
        return new int[][]{
                {Item.WOODEN_PRESSURE_PLATE, 0, 1}
        };
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

}
