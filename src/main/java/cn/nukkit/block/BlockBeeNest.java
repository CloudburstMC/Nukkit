package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

public class BlockBeeNest extends BlockBeeHive {

    public BlockBeeNest () {
        this(0);
    }
    protected BlockBeeNest(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Bee Nest";
    }

    @Override
    public int getId() {
        return BEE_NEST;
    }

    @Override
    public double getHardness() {
        return 0.3;
    }

    @Override
    public double getResistance() {
        return 1.5;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

}
