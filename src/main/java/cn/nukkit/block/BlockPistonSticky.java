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
    public int getPistonHeadBlockId() {
        return PISTON_HEAD_STICKY;
    }

    @Override
    public String getName() {
        return "Sticky Piston";
    }
}
