package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.DAYLIGHT_DETECTOR;

/**
 * Created on 2015/11/22 by CreeperFace.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockDaylightDetectorInverted extends BlockDaylightDetector {

    public BlockDaylightDetectorInverted(Identifier id) {
        super(id);
    }

    @Override
    public Item toItem() {
        return Item.get(DAYLIGHT_DETECTOR);
    }

    protected boolean invertDetect() {
        return true;
    }

    @Override
    public boolean canWaterlogSource() {
        return true;
    }
}
