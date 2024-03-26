package cn.nukkit.block;

public class BlockButtonSpruce extends BlockButtonWooden {

    public BlockButtonSpruce() {
        this(0);
    }

    public BlockButtonSpruce(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Spruce Button";
    }

    @Override
    public int getId() {
        return SPRUCE_BUTTON;
    }
}
