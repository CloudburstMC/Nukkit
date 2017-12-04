package cn.nukkit.server.block;

/**
 * Created on 2015/11/22 by CreeperFace.
 * Package cn.nukkit.server.block in project Nukkit .
 */
public class BlockDaylightDetectorInverted extends BlockDaylightDetector {

    public BlockDaylightDetectorInverted() {
        this(0);
    }

    public BlockDaylightDetectorInverted(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DAYLIGHT_DETECTOR_INVERTED;
    }

    @Override
    public String getName() {
        return "Daylight Detector Inverted";
    }

    protected boolean invertDetect() {
        return true;
    }

}
