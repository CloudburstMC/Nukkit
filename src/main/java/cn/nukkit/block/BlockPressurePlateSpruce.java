package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockPressurePlateSpruce extends BlockPressurePlateWood {

    public BlockPressurePlateSpruce() {
        this(0);
    }

    public BlockPressurePlateSpruce(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Spruce Pressure Plate";
    }

    @Override
    public int getId() {
        return SPRUCE_PRESSURE_PLATE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SPRUCE_BLOCK_COLOR;
    }
}
