package cn.nukkit.block;

import cn.nukkit.utils.Identifier;

/**
 * @author Nukkit Project Team
 */
public class BlockMushroomBrown extends BlockMushroom {

    public BlockMushroomBrown(Identifier id) {
        super(id);
    }

    @Override
    public int getLightLevel() {
        return 1;
    }

    @Override
    protected int getType() {
        return 0;
    }
}
