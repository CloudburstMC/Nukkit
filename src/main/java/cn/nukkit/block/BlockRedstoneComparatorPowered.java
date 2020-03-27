package cn.nukkit.block;

import cn.nukkit.utils.Identifier;

/**
 * @author CreeperFace
 */
public class BlockRedstoneComparatorPowered extends BlockRedstoneComparator {

    public BlockRedstoneComparatorPowered(Identifier id) {
        super(id);
        this.isPowered = true;
    }

    @Override
    protected BlockRedstoneComparator getPowered() {
        return this;
    }
}
