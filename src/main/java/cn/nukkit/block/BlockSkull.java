package cn.nukkit.block;

/**
 * author: Justin
 */

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;


public class BlockSkull extends BlockTransparent{
    public static final int SKELETON_SKULL = 0;
    public static final int WITHER_SKELETON_SKULL = 1;
    public static final int ZOMBIE_HEAD = 2;
    public static final int HEAD = 3;
    public static final int CREEPER_HEAD = 4;

    public BlockSkull() {
        this(0);
    }

    public BlockSkull(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SKULL_BLOCK;
    }

    @Override
    public double getHardness() {
        return 1;
    }

    @Override
    public boolean isSolid(){
        return false;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "Skeleton Skull",
                "Wither Skeleton Skull",
                "Zombie Head",
                "Head",
                "Creeper Head"
        };

        return names[this.meta & 0x04];
    }


    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz) {
        return this.place(item, block, target, face, fx, fy, fz, null);
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        short[] faces = new short[]{
                0,75,
                0,5,
                0,75,
                0b0000,75,
                0b0000,5,
                0b0000,75
        };

        this.meta = ((this.meta & 0x04) | faces[face]);
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

    public double getResistance(){
        return 5;
    }
    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{new int[]{this.getId(), this.meta & 0x04, 1}};
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }
}