package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCherryTrapdoor extends BlockTrapdoor {

    public BlockCherryTrapdoor() {
        this(0);
    }

    public BlockCherryTrapdoor(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Cherry Trapdoor";
    }

    @Override
    public int getId() {
        return CHERRY_TRAPDOOR;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WHITE_TERRACOTA_BLOCK_COLOR;
    }
}
