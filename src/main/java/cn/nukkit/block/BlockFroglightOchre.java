package cn.nukkit.block;

public class BlockFroglightOchre extends BlockFroglight {

    public BlockFroglightOchre() {
        this(0);
    }

    public BlockFroglightOchre(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Ochre Froglight";
    }

    @Override
    public int getId() {
        return OCHRE_FROGLIGHT;
    }
}
