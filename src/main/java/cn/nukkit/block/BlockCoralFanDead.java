package cn.nukkit.block;

import cn.nukkit.level.Level;
import cn.nukkit.utils.BlockColor;

public class BlockCoralFanDead extends BlockCoralFan {

    public BlockCoralFanDead() {
        this(0);
    }

    public BlockCoralFanDead(int meta) {
        super(meta);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!this.getSide(this.getRootsFace()).isSolid()) {
                this.getLevel().useBreakOn(this);
            }
            return type;
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            return super.onUpdate(type);
        }
        return 0;
    }

    @Override
    public String getName() {
        return "Dead " + super.getName();
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRAY_BLOCK_COLOR;
    }

    @Override
    public int getId() {
        return CORAL_FAN_DEAD;
    }

    @Override
    public boolean isDead() {
        return true;
    }
}
