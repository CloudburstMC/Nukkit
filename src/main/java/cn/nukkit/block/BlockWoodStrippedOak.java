package cn.nukkit.block;

public class BlockWoodStrippedOak extends BlockWoodStripped {
    public BlockWoodStrippedOak() {
        this(0);
    }
    
    public BlockWoodStrippedOak(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return STRIPPED_OAK_LOG;
    }
    
    @Override
    public String getName() {
        return "Stripped Oak Log";
    }
}
