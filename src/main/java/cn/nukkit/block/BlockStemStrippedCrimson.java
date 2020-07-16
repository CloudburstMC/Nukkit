package cn.nukkit.block;

public class BlockStemStrippedCrimson extends BlockStemStripped {
    public BlockStemStrippedCrimson() {
        this(0);
    }
    
    public BlockStemStrippedCrimson(int meta) {
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

}
