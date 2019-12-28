package cn.nukkit.block;

public class BlockWoodStrippedSpruce extends BlockWoodStripped {
    public BlockWoodStrippedSpruce() {
        this(0);
    }
    
    public BlockWoodStrippedSpruce(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return STRIPPED_SPRUCE_LOG;
    }
    
    @Override
    public String getName() {
        return "Stripped Spruce Log";
    }
}
