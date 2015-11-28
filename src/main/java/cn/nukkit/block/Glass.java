package cn.nukkit.block;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class Glass extends Transparent {

    public Glass() {
        this(Block.GLASS);
    }

    public Glass(int id) {
        super(id);
    }

    public Glass(int id, int meta) {
        super(id, meta);
    }

    @Override
    public String getName() {
        return "Glass";
    }

    @Override
    public double getHardness() {
        return 0.3;
    }

}
