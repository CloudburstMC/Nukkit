package cn.nukkit.block;

public class BlockTrapdoorBirch extends BlockTrapdoor {
    public BlockTrapdoorBirch() {
        this(0);
    }
    
    public BlockTrapdoorBirch(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return BIRCH_TRAPDOOR;
    }
    
    @Override
    public String getName() {
        return "Birch Trapdoor";
    }
}
