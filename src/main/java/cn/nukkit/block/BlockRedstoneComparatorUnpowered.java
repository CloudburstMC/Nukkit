package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitDifference;

/**
 * @author CreeperFace
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
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
