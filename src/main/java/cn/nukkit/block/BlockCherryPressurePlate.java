package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCherryPressurePlate extends BlockPressurePlateWood {

    public BlockCherryPressurePlate() {
        this(0);
    }

    public BlockCherryPressurePlate(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Cherry Pressure Plate";
    }

    @Override
    public int getId() {
        return CHERRY_PRESSURE_PLATE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WHITE_TERRACOTA_BLOCK_COLOR;
    }
}
