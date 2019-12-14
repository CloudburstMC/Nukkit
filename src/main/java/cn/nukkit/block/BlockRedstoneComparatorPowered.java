package cn.nukkit.block;

/**
 * @author CreeperFace
 */
public class BlockRedstoneComparatorPowered extends BlockRedstoneComparator {

    public BlockRedstoneComparatorPowered(int id, int meta) {
        super(id, meta);
        this.isPowered = true;
    }

    @Override
    public String getName() {
        return "Comparator Block Powered";
    }

    @Override
    protected BlockRedstoneComparator getPowered() {
        return this;
    }
}
