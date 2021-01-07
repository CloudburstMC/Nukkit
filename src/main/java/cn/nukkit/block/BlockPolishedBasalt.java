package cn.nukkit.block;

public class BlockPolishedBasalt extends BlockBasalt {

    public BlockPolishedBasalt() { this(0); }

    public BlockPolishedBasalt(int meta) { super(meta); }

    @Override
    public String getName() {
        return "Polished Basalt";
    }

    @Override
    public int getId() {
        return BlockID.POLISHED_BASALT;
    }
}
