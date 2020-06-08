package cn.nukkit.block;

public class BlockPressurePlateDarkOak extends BlockPressurePlateWood {
    
    public BlockPressurePlateDarkOak() {
        this(0);
    }
    
    public BlockPressurePlateDarkOak(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return DARK_OAK_PRESSURE_PLATE;
    }
    
    @Override
    public String getName() {
        return "Dark Oak Pressure Plate";
    }
}
