package cn.nukkit.block;

public class BlockCoralFanHang3 extends BlockCoralFanHang {

    public BlockCoralFanHang3() {
        this(0);
    }

    public BlockCoralFanHang3(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CORAL_FAN_HANG3;
    }

    @Override
    public int getType() {
        return BlockCoral.TYPE_HORN;
    }
}
