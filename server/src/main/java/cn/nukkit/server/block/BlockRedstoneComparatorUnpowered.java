package cn.nukkit.server.block;

/**
 * @author CreeperFace
 */
public class BlockRedstoneComparatorUnpowered extends BlockRedstoneComparator {

    public BlockRedstoneComparatorUnpowered() {
        this(0);
    }

    public BlockRedstoneComparatorUnpowered(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return UNPOWERED_COMPARATOR;
    }

    @Override
    public String getName() {
        return "Comparator Block Unpowered";
    }

    @Override
    protected BlockRedstoneComparator getUnpowered() {
        return this;
    }
}