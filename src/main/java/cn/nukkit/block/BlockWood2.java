package cn.nukkit.block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockWood2 extends BlockWood {
    public static final int ACACIA = 1;
    public static final int DARK_OAK = 2;

    public BlockWood2() {
        super();
    }

    public BlockWood2(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WOOD2;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "Acacia Wood",
                "Dark Oak Wood",
                ""
        };

        return names[this.meta & 0x03];
    }

}
