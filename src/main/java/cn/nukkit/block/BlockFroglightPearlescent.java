package cn.nukkit.block;

public class BlockFroglightPearlescent extends BlockFroglight {

    public BlockFroglightPearlescent() {
        this(0);
    }

    public BlockFroglightPearlescent(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Pearlescent Froglight";
    }

    @Override
    public int getId() {
        return PEARLESCENT_FROGLIGHT;
    }
}
