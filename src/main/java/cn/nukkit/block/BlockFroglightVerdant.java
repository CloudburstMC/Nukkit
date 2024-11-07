package cn.nukkit.block;

public class BlockFroglightVerdant extends BlockFroglight {

    public BlockFroglightVerdant() {
        this(0);
    }

    public BlockFroglightVerdant(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Verdant Froglight";
    }

    @Override
    public int getId() {
        return VERDANT_FROGLIGHT;
    }
}
