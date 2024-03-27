package cn.nukkit.block;

public class BlockCoralFanHang2 extends BlockCoralFanHang {

    public BlockCoralFanHang2() {
        this(0);
    }

    public BlockCoralFanHang2(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CORAL_FAN_HANG2;
    }

    @Override
    public int getType() {
        if ((this.getDamage() & 0b1) == 0) {
            return BlockCoral.TYPE_BUBBLE;
        } else {
            return BlockCoral.TYPE_FIRE;
        }
    }
}
