package cn.nukkit.block;

/**
 * @author Nukkit Project Team
 */
public class BlockRailActivator extends BlockRail {

    public BlockRailActivator(int meta) {
        super(meta);
    }

    public BlockRailActivator() {
        this(0);
    }

    @Override
    public String getName() {
        return "Activator Rail";
    }

    @Override
    public int getId() {
        return ACTIVATOR_RAIL;
    }
}
