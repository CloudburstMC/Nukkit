package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockWood2 extends BlockWood {

    public static final int ACACIA = 0;
    public static final int DARK_OAK = 1;

    private static final String[] NAMES = new String[]{
            "Acacia Wood",
            "Dark Oak Wood",
            ""
    };

    public BlockWood2() {
        this(0);
    }

    public BlockWood2(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WOOD2;
    }

    @Override
    public String getName() {
        return NAMES[this.getDamage() > 2 ? 0 : this.getDamage()];
    }

    @Override
    public BlockColor getColor() {
        switch(getDamage() & 0x07){
            case ACACIA:
                return BlockColor.ORANGE_BLOCK_COLOR;
            case DARK_OAK:
                return BlockColor.BROWN_BLOCK_COLOR;
            default:
                return BlockColor.WOOD_BLOCK_COLOR;
        }
    }
}
