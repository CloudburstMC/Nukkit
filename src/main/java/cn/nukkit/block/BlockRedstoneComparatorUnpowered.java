package cn.nukkit.block;

/**
 * @author CreeperFace
 */
public class BlockRedstoneComparatorUnpowered extends BlockRedstoneComparator {

    public BlockRedstoneComparatorUnpowered(int id, int meta) {
        super(id, meta);
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