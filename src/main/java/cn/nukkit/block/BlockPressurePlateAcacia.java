package cn.nukkit.block;

public class BlockPressurePlateAcacia extends BlockPressurePlateWood {
    
    public BlockPressurePlateAcacia() {
        this(0);
    }
    
    public BlockPressurePlateAcacia(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return ACACIA_PRESSURE_PLATE;
    }
    
    @Override
    public String getName() {
        return "Acacia Pressure Plate";
    }
}
