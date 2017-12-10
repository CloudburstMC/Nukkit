package cn.nukkit.server.block;

/**
 * @author CreeperFace
 */
public class BlockRedstoneComparatorPowered extends BlockRedstoneComparator {

    public BlockRedstoneComparatorPowered() {
        this(0);
    }

    public BlockRedstoneComparatorPowered(int meta) {
        super(meta);
        this.isPowered = true;
    }

    @Override
    public int getId() {
        return POWERED_COMPARATOR;
    }

    @Override
    public String getName() {
        return "Comparator BlockType Powered";
    }

    @Override
    protected BlockRedstoneComparator getPowered() {
        return this;
    }
}
