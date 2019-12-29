package cn.nukkit.block;

public class BlockTrapdoorSpruce extends BlockTrapdoor {
    public BlockTrapdoorSpruce() {
        this(0);
    }
    
    public BlockTrapdoorSpruce(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return SPRUCE_TRAPDOOR;
    }
    
    @Override
    public String getName() {
        return "Spruce Trapdoor";
    }
}
