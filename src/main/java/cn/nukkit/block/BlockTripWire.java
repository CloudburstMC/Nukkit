package cn.nukkit.block;

import cn.nukkit.math.AxisAlignedBB;

/**
 * Created by CreeperFace on 1. 1. 2017.
 */
public class BlockTripWire extends BlockTransparent {

    public BlockTripWire(int meta) {
        super(meta);
    }

    public BlockTripWire() {
        this(0);
    }

    @Override
    public int getId() {
        return TRIPWIRE;
    }

    @Override
    public String getName() {
        return "Tripwire";
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }
}
