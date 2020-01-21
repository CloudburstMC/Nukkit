package cn.nukkit.block;

public class BlockWoodStrippedJungle extends BlockWoodStripped {
    public BlockWoodStrippedJungle() {
        this(0);
    }
    
    public BlockWoodStrippedJungle(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return STRIPPED_JUNGLE_LOG;
    }
    
    @Override
    public String getName() {
        return "Stripped Jungle Log";
    }
}
