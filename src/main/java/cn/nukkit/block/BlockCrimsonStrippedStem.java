package cn.nukkit.block;

public class BlockCrimsonStrippedStem extends BlockWoodStripped {
    public BlockCrimsonStrippedStem() {
        this(0);
    }
    
    public BlockCrimsonStrippedStem(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return STRIPPED_CRIMSON_STEM;
    }
    
    @Override
    public String getName() {
        return "Stripped Crimson Stem";
    }
    
    @Override
    public int getBurnChance() {
        return 0;
    }
    
    @Override
    public int getBurnAbility() {
        return 0;
    }
}
