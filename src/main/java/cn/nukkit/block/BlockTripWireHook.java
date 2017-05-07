package cn.nukkit.block;

/**
 * @author CreeperFace
 */
public class BlockTripWireHook extends BlockFlowable {

    public BlockTripWireHook() {
        this(0);
    }

    public BlockTripWireHook(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Tripwire Hook";
    }

    @Override
    public int getId() {
        return TRIPWIRE_HOOK;
    }

    //TODO: redstone
}
