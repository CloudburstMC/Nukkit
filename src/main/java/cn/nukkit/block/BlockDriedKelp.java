package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

public class BlockDriedKelp extends BlockSolid {
    public BlockDriedKelp(Identifier id) {
        super(id);
    }

    @Override
    public double getHardness() {
        return 0.5f;
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GREEN_BLOCK_COLOR;
    }
}
