package cn.nukkit.block;

/**
 * @author CreeperFace
 */
public class BlockPiston extends BlockPistonBase {

    public BlockPiston() {
        this(0);
    }

    public BlockPiston(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PISTON;
    }

    @Override
    public String getName() {
        return "Piston";
    }

    @Override
    public int getPistonHeadBlockId() {
        return PISTON_HEAD;
    }
}
