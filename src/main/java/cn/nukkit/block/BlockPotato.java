package cn.nukkit.block;

import cn.nukkit.item.Item;

import java.util.Random;

/**
 * Created by Pub4Game on 15.01.2016.
 */
public class BlockPotato extends BlockCrops {

    public BlockPotato(int meta) {
        super(meta);
    }

    public BlockPotato() {
        this(0);
    }

    @Override
    public String getName() {
        return "Potato Block";
    }

    @Override
    public int getId() {
        return Block.POTATO_BLOCK;
    }

    @Override
    public int[][] getDrops(Item item) {
        if (meta >= 0x07) {
            return new int[][]{{Item.POTATO, 0, new Random().nextInt(3) + 1}};
        }
        return new int[][]{
                {Item.POTATO, 0, 1}
        };
    }
}
