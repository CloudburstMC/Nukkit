package cn.nukkit.block;

import cn.nukkit.utils.Identifier;

public class BlockBlastFurnace extends BlockBlastFurnaceBurning {
    public BlockBlastFurnace(Identifier id) {
        super(id);
    }

    @Override
    public int getLightLevel() {
        return 0;
    }
}
