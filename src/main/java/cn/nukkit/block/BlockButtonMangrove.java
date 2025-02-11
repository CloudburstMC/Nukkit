package cn.nukkit.block;

public class BlockButtonMangrove extends BlockButtonWooden {

    public BlockButtonMangrove() {
        this(0);
    }

    public BlockButtonMangrove(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Mangrove Button";
    }

    @Override
    public int getId() {
        return MANGROVE_BUTTON;
    }
}
