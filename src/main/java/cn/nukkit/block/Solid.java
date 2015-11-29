package cn.nukkit.block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Solid extends Block {

    protected Solid(int meta) {
        super(meta);
    }

    @Override
    public boolean isSolid() {
        return true;
    }
}
