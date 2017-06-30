package cn.nukkit.block;

/**
 * Created by Snake1999 on 2016/1/11.
 * Package cn.nukkit.block in project nukkit
 */
public class BlockRailPowered extends BlockRail {
    public BlockRailPowered() {
        this(0);
    }

    public BlockRailPowered(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return POWERED_RAIL;
    }

    @Override
    public String getName() {
        return "Powered Rail";
    }
}
