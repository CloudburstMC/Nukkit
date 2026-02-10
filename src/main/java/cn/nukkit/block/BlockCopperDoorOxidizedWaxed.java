package cn.nukkit.block;

public class BlockCopperDoorOxidizedWaxed extends BlockCopperDoorOxidized {

    public BlockCopperDoorOxidizedWaxed() {
        this(0);
    }

    public BlockCopperDoorOxidizedWaxed(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Waxed Oxidized Copper Door";
    }

    @Override
    public int getId() {
        return WAXED_OXIDIZED_COPPER_DOOR_BLOCK;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
