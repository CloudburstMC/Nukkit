package cn.nukkit.block;

import cn.nukkit.utils.Identifier;

/**
 * @author CreeperFace
 */
public class BlockRedstoneComparatorUnpowered extends BlockRedstoneComparator {

    public BlockRedstoneComparatorUnpowered(Identifier id) {
        super(id);
    }

    @Override
    protected BlockRedstoneComparator getUnpowered() {
        return this;
    }
}