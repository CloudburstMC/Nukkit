package cn.nukkit.block;

public class BlockButtonCrimson extends BlockButtonWooden {

    public BlockButtonCrimson() {
        this(0);
    }

    public BlockButtonCrimson(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Crimson Button";
    }

    @Override
    public int getId() {
        return CRIMSON_BUTTON;
    }
}
