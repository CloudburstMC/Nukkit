package cn.nukkit.block;

import cn.nukkit.utils.Identifier;

public class BlockSmoker extends BlockSmokerBurning {
    public BlockSmoker(Identifier id) {
        super(id);
    }

    @Override
    public int getLightLevel() {
        return 0;
    }
}
