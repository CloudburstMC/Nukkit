package cn.nukkit.block;

/**
 * @author CreeperFace
 */
public class BlockPiston extends BlockPistonBase {

    public BlockPiston(int id, int meta) {
        super(id, meta);
    }

    @Override
    public String getName() {
        return "Piston";
    }
}
