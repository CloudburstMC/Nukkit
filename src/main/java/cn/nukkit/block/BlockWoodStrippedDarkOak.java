package cn.nukkit.block;

public class BlockWoodStrippedDarkOak extends BlockWoodStripped {
    public BlockWoodStrippedDarkOak() {
        this(0);
    }
    
    public BlockWoodStrippedDarkOak(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return STRIPPED_DARK_OAK_LOG;
    }
    
    @Override
    public String getName() {
        return "Stripped Dark Oak Log";
    }
}
