package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockPressurePlateBirch extends BlockPressurePlateWood {

    public BlockPressurePlateBirch() {
        this(0);
    }

    public BlockPressurePlateBirch(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Birch Pressure Plate";
    }

    @Override
    public int getId() {
        return BIRCH_PRESSURE_PLATE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }
}
