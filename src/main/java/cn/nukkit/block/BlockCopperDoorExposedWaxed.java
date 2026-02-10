package cn.nukkit.block;

public class BlockCopperDoorExposedWaxed extends BlockCopperDoorExposed {

    public BlockCopperDoorExposedWaxed() {
        this(0);
    }

    public BlockCopperDoorExposedWaxed(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Waxed Exposed Copper Door";
    }

    @Override
    public int getId() {
        return WAXED_EXPOSED_COPPER_DOOR_BLOCK;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
