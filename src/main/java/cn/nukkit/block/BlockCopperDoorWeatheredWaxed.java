package cn.nukkit.block;

public class BlockCopperDoorWeatheredWaxed extends BlockCopperDoorWeathered {

    public BlockCopperDoorWeatheredWaxed() {
        this(0);
    }

    public BlockCopperDoorWeatheredWaxed(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Waxed Weathered Copper Door";
    }

    @Override
    public int getId() {
        return WAXED_WEATHERED_COPPER_DOOR_BLOCK;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
