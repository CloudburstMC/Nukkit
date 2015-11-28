package cn.nukkit.block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Transparent extends Block {

    protected Transparent(int meta) {
        super(meta);
    }

    @Override
    public boolean isTransparent() {
        return true;
    }
}
