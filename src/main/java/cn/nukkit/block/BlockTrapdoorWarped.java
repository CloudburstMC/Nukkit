package cn.nukkit.block;

public class BlockTrapdoorWarped extends BlockTrapdoor {
    public BlockTrapdoorWarped() {
        this(0);
    }
    
    public BlockTrapdoorWarped(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return WARPED_TRAPDOOR;
    }
    
    @Override
    public String getName() {
        return "Warped Trapdoor";
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
