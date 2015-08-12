package cn.nukkit.block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Solid extends Block {
    public Solid(int id) {
        super(id);
    }

    public Solid(int id, int meta) {
        super(id, meta);
    }

    @Override
    public boolean isSolid() {
        return true;
    }
}
