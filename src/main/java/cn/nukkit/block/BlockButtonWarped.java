package cn.nukkit.block;

public class BlockButtonWarped extends BlockButtonWooden {
    public BlockButtonWarped() {
        this(0);
    }
    
    public BlockButtonWarped(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return WARPED_BUTTON;
    }
    
    @Override
    public String getName() {
        return "Warped Button";
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
