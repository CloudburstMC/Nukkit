package cn.nukkit.block;

import cn.nukkit.level.Level;
import cn.nukkit.utils.RGBColor;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class DeadBush extends Flowable {
    public DeadBush() {
        this(0);
    }

    public DeadBush(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Dead Bush";
    }

    @Override
    public int getId() {
        return DEAD_BUSH;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.getSide(0).isTransparent()) {
                this.getLevel().useBreakOn(this);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public RGBColor getMapColor() {
        return RGBColor.foliageColor;
    }
}
