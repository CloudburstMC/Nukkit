package cn.nukkit.block;

import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

public class BlockVinesTwisting extends BlockVinesNether {

    public BlockVinesTwisting() {
        this(0);
    }

    public BlockVinesTwisting(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Twisting Vines";
    }

    @Override
    public int getId() {
        return TWISTING_VINES;
    }

    @Override
    public BlockFace getGrowthDirection() {
        return BlockFace.UP;
    }

    @Override
    public int getVineAge() {
        return this.getDamage();
    }

    @Override
    public void setVineAge(int vineAge) {
        this.setDamage(vineAge & 0x19);
    }

    @Override
    public int getMaxVineAge() {
        return 25;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
    }
}
