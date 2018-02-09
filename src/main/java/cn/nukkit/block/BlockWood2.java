package cn.nukkit.block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockWood2 extends BlockWood {

    public static final int ACACIA = 0;
    public static final int DARK_OAK = 1;

    public BlockWood2() {
        this(0);
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

        return names[this.getDamage() & 0x03];
    }

}
