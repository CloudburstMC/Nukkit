package cn.nukkit.block;

public class BlockPistonHeadSticky extends BlockPistonHead {
    public BlockPistonHeadSticky() {
        this(0);
    }
    
    public BlockPistonHeadSticky(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return PISTON_HEAD_STICKY;
    }
    
    @Override
    public String getName() {
        return "Sticky Piston Head";
    }
}
