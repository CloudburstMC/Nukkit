package cn.nukkit.block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockWood2 extends BlockWood {

    public static final int ACACIA = 0;
    public static final int DARK_OAK = 1;

    private static final String[] NAMES = new String[]{
            "Acacia Wood",
            "Dark Oak Wood",
            ""
    };

    public BlockWood2(int id, int meta) {
        super(id, meta);
    }

    @Override
    public String getName() {
        return NAMES[this.getDamage() > 2 ? 0 : this.getDamage()];
    }
}
