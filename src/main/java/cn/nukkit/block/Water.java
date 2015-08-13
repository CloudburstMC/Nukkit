package cn.nukkit.block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Water extends Liquid {

    protected int id = WATER;

    public Water() {
        this(0);
    }

    public Water(int meta) {
        super(WATER, meta);
        this.meta = meta;
    }

    @Override
    public String getName() {
        return "Water";
    }

    //todo
}
