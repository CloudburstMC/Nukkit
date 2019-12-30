package cn.nukkit.block;

import cn.nukkit.utils.Identifier;

/**
 * Created by Pub4Game on 03.01.2015.
 */
public class BlockMushroomRed extends BlockMushroom {

    public BlockMushroomRed(Identifier id) {
        super(id);
    }

    @Override
    protected int getType() {
        return 1;
    }
}
