package cn.nukkit.block;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class StillLava extends Lava{

    public StillLava() {
        super(0);
    }

    public StillLava(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Still Lava";
    }
}
