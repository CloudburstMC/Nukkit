package cn.nukkit.block;

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
}
