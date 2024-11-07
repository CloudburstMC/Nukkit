package cn.nukkit.block;

import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

public class BlockWeepingVines extends BlockVinesNether {

    public BlockWeepingVines() {
        this(0);
    }

    public BlockWeepingVines(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Weeping Vines";
    }

    @Override
    public int getId() {
        return WEEPING_VINES;
    }

    @Override
    public BlockFace getGrowthDirection() {
        return BlockFace.DOWN;
    }

    @Override
    public int getVineAge() {
        return this.getDamage();
    }

    @Override
    public void setVineAge(int vineAge) {
        this.setDamage(vineAge & 0x19);
    }

    public int getMaxVineAge() {
        return 25;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHERRACK_BLOCK_COLOR;
    }
}
