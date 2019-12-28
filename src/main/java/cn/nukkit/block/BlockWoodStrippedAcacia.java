package cn.nukkit.block;

public class BlockWoodStrippedAcacia extends BlockWoodStripped {
    public BlockWoodStrippedAcacia() {
        this(0);
    }
    
    public BlockWoodStrippedAcacia(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return STRIPPED_ACACIA_LOG;
    }
    
    @Override
    public String getName() {
        return "Stripped Acacia Log";
    }
}
