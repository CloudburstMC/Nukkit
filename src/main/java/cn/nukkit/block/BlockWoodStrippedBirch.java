package cn.nukkit.block;

public class BlockWoodStrippedBirch extends BlockWoodStripped {
    public BlockWoodStrippedBirch() {
        this(0);
    }
    
    public BlockWoodStrippedBirch(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return STRIPPED_BIRCH_LOG;
    }
    
    @Override
    public String getName() {
        return "Stripped Birch Log";
    }
}
