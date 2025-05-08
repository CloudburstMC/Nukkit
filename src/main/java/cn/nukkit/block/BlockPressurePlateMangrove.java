package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockPressurePlateMangrove extends BlockPressurePlateWood {

    public BlockPressurePlateMangrove() {
        this(0);
    }

    public BlockPressurePlateMangrove(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Mangrove Pressure Plate";
    }

    @Override
    public int getId() {
        return MANGROVE_PRESSURE_PLATE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }
}
