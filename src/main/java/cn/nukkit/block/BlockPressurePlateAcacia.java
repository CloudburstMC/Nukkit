package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockPressurePlateAcacia extends BlockPressurePlateWood {

    public BlockPressurePlateAcacia() {
        this(0);
    }

    public BlockPressurePlateAcacia(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Acacia Pressure Plate";
    }

    @Override
    public int getId() {
        return ACACIA_PRESSURE_PLATE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
