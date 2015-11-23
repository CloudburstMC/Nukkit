package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class Sapling extends Flowable {
    public static final int OAK = 0;
    public static final int SPRUCE = 1;
    public static final int BRICH = 2;
    public static final int JUNGLE = 3;
    public static final int ACACIA = 4;
    public static final int DARK_OAK = 5;

    public Sapling() {
        this(0);
    }

    public Sapling(int meta) {
        super(SAPLING, meta);
    };

    @Override
    public String getName() {
        String[] names = new String[]{
                "Oak Sapling",
                "Spruce Sapling",
                "Birch Sapling",
                "Jungle Sapling",
                "Acacia Sapling",
                "Dark Oak Sapling",
                "",
                ""
        };
        return names[this.meta & 0x07];
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player)
    {
        Block down = this.getSide(Block.SIDE_DOWN);
        if(down.getId() == Block.GRASS || down.getId() == Block.DIRT || down.getId() == Block.FARMLAND)
        {
            this.getLevel().setBlock(block,this,true,true);
            return true;
        }

        return false;
    }

    @Override
    public boolean canBeActivated()
    {
        return true;
    }

    //todo:onActivate, onUpdate

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{new int[]{Item.SAPLING, this.getDamage(), 1}};
    }

}
