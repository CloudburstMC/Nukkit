package cn.nukkit.block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Water extends Liquid {


    public Water() {
        this(0);
    }

    public Water(int meta) {
        super(WATER, meta);
    }

    @Override
    public String getName() {
        return "Water";
    }

    //todo
}
