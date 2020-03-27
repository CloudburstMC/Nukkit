package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

/**
 * @author Erik Miller | EinBexiii | Bex
 */
public class BlockStairsPrismarineBricks extends BlockStairsPrismarine {

    public BlockStairsPrismarineBricks(Identifier id) {
        super(id);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIAMOND_BLOCK_COLOR;
    }
}
