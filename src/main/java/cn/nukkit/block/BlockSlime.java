package cn.nukkit.block;

/**
 * Created by Pub4Game on 21.02.2016.
 */
public class BlockSlime extends BlockSolid {

    public BlockSlime() {
        this(0);
    }

    public BlockSlime(int meta) {
        super(meta);
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public String getName() {
        return "Slime Block";
    }

    @Override
    public int getId() {
        return SLIME_BLOCK;
    }

    @Override
    public double getResistance() {
        return 0;
    }
}
