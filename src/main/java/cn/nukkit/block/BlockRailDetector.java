package cn.nukkit.block;

/**
 * Created on 2015/11/22 by CreeperFace.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockRailDetector extends BlockRail {
    public BlockRailDetector() {
        this(0);
    }

    public BlockRailDetector(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DETECTOR_RAIL;
    }

    @Override
    public String getName() {
        return "Detector Rail";
    }
}
