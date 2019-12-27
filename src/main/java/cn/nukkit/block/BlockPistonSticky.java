package cn.nukkit.block;

/**
 * @author CreeperFace
 */
public class BlockPistonSticky extends BlockPistonBase {

    public BlockPistonSticky() {
        this(0);
    }

    public BlockPistonSticky(int meta) {
        super(meta);
        this.sticky = true;
    }

    @Override
    public int getId() {
        return STICKY_PISTON;
    }

    @Override
    public String getName() {
        return "Sticky Piston";
    }
    
    @Override
    protected BlockPistonHead createHead(int damage) {
        return new BlockPistonHeadSticky(damage);
    }
}
