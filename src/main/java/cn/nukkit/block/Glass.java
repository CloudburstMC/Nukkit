package cn.nukkit.block;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class Glass extends Transparent {

    public Glass() {
        this(0);
    }

    public Glass(int meta) {
        super(GLASS, meta);
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
