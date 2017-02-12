package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockDragonEgg extends BlockTransparent {

    public BlockDragonEgg() {
        this(0);
    }

    public BlockDragonEgg(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Dragon Egg";
    }

    @Override
    public int getId() {
        return DRAGON_EGG;
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 45;
    }

    @Override
    public int getLightLevel() {
        return 1;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.OBSIDIAN_BLOCK_COLOR;
    }
}