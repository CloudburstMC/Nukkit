package cn.nukkit.block;

import cn.nukkit.utils.Identifier;

/**
 * Created on 2015/12/8 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockPumpkinLit extends BlockPumpkin {
    public BlockPumpkinLit(Identifier id) {
        super(id);
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

}
