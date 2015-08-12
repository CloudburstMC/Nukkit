package cn.nukkit.block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Trasparent extends Block {

    public Trasparent(int id) {
        super(id);
    }

    public Trasparent(int id, int meta) {
        super(id, meta);
    }

    @Override
    public boolean isTransparent() {
        return true;
    }
}
