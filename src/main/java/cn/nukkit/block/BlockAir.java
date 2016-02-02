package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.math.AxisAlignedBB;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockAir extends BlockTransparent {

    public BlockAir() {
        this(0);
    }

    public BlockAir(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return AIR;
    }

    @Override
    public String getName() {
        return "AIR";
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    public boolean canBePlaced() {
        return false;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0;
    }
}
