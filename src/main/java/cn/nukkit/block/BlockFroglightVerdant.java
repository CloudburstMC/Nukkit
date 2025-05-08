package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockFroglightVerdant extends BlockFroglight {

    public BlockFroglightVerdant() {
        this(0);
    }

    public BlockFroglightVerdant(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Verdant Froglight";
    }

    @Override
    public int getId() {
        return VERDANT_FROGLIGHT;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GLOW_LICHEN_BLOCK_COLOR;
    }
}
