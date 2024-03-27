package cn.nukkit.block;

public class BlockButtonDarkOak extends BlockButtonWooden {

    public BlockButtonDarkOak() {
        this(0);
    }

    public BlockButtonDarkOak(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Dark Oak Button";
    }

    @Override
    public int getId() {
        return DARK_OAK_BUTTON;
    }
}
