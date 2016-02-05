package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockWood extends BlockSolid {
    public static final int OAK = 0;
    public static final int SPRUCE = 1;
    public static final int BIRCH = 2;
    public static final int JUNGLE = 3;
    //public static final int ACACIA = 4;
    //public static final int DARK_OAK = 5;


    public BlockWood() {
        this(0);
    }

    public BlockWood(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WOOD;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "Oak Wood",
                "Spruce Wood",
                "Birch Wood",
                "Jungle Wood"
        };

        return names[this.meta & 0x03];
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz) {
        return this.place(item, block, target, face, fx, fy, fz, null);
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        short[] faces = new short[]{
                0,
                0,
                0b1000,
                0b1000,
                0b0100,
                0b0100
        };

        this.meta = ((this.meta & 0x03) | faces[face]);
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{new int[]{this.getId(), this.meta & 0x03, 1}};
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }
}
