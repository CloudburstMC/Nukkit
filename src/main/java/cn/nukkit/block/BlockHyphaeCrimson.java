package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockHyphaeCrimson extends BlockStem {

    public BlockHyphaeCrimson() {
        this(0);
    }

    public BlockHyphaeCrimson(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CRIMSON_HYPHAE;
    }

    @Override
    public int getStrippedId() {
        return STRIPPED_CRIMSON_HYPHAE;
    }

    @Override
    public String getName() {
        return "Crimson Hyphae";
    }

    @Override
    public double getHardness() {
        return 0.3; // 2
    }

    @Override
    public double getResistance() {
        return 2;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_HYPHAE_BLOCK_COLOR;
    }
}
