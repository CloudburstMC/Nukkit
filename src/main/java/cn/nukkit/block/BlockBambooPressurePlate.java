package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockBambooPressurePlate extends BlockPressurePlateWood {

    public BlockBambooPressurePlate() {
        this(0);
    }

    public BlockBambooPressurePlate(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Bamboo Pressure Plate";
    }

    @Override
    public int getId() {
        return BAMBOO_PRESSURE_PLATE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }
}
