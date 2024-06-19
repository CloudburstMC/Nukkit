package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockPressurePlateDarkOak extends BlockPressurePlateWood {

    public BlockPressurePlateDarkOak() {
        this(0);
    }

    public BlockPressurePlateDarkOak(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Dark Oak Pressure Plate";
    }

    @Override
    public int getId() {
        return DARK_OAK_PRESSURE_PLATE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWN_BLOCK_COLOR;
    }
}
