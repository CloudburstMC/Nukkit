package cn.nukkit.block;

public class BlockPressurePlateCrimson extends BlockPressurePlateWood {

    public BlockPressurePlateCrimson() {
        this(0);
    }

    public BlockPressurePlateCrimson(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Crimson Pressure Plate";
    }

    @Override
    public int getId() {
        return CRIMSON_PRESSURE_PLATE;
    }
}
