package cn.nukkit.block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Transparent extends Block {

    public Transparent(int id) {
        super(id);
    }

    public Transparent(int id, int meta) {
        super(id, meta);
    }

    @Override
    public boolean isTransparent() {
        return true;
    }
}
