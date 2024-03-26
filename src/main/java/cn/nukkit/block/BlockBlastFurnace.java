package cn.nukkit.block;

public class BlockBlastFurnace extends BlockBlastFurnaceLit {

    public BlockBlastFurnace() {
        this(0);
    }

    public BlockBlastFurnace(int meta) {
        super(meta);
    }

    @Override
    public int getLightLevel() {
        return 0;
    }

    @Override
    public String getName() {
        return "Blast Furnace";
    }

    @Override
    public int getId() {
        return BLAST_FURNACE;
    }
}
