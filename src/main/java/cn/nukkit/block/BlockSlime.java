package cn.nukkit.block;

import cn.nukkit.item.Item;

/**
 * Created by Pub4Game on 21.02.2016.
 */
public class BlockSlime extends BlockSolid {

    public BlockSlime() {
        this(0);
    }

    public BlockSlime(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Slime Block";
    }

    @Override
    public int getId() {
        return SLIME_BLOCK;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{{Item.SLIME_BLOCK, 0, 1}};
    }
}
