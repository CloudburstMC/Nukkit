package cn.nukkit.block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Lava extends Liquid {


    public Lava() {
        this(0);
    }

    public Lava(int meta) {
        super(LAVA, meta);
    }

    @Override
    public String getName() {
        return "Lava";
    }

    //todo
}
