package cn.nukkit.block;

public class BlockCopperDoorWaxed extends BlockCopperDoor {

    public BlockCopperDoorWaxed() {
        this(0);
    }

    public BlockCopperDoorWaxed(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Waxed Copper Door";
    }

    @Override
    public int getId() {
        return WAXED_COPPER_DOOR_BLOCK;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
