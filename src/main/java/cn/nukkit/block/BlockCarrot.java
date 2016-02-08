package cn.nukkit.block;

import cn.nukkit.item.Item;

import java.util.Random;

/**
 * @author Nukkit Project Team
 */
public class BlockCarrot extends BlockCrops {

    public BlockCarrot(int meta) {
        super(meta);
    }

    public BlockCarrot() {
        this(0);
    }

    @Override
    public String getName() {
        return "Carrot Block";
    }

    @Override
    public int getId() {
        return Block.CARROT_BLOCK;
    }

    @Override
    public int[][] getDrops(Item item) {
        if (meta >= 0x07) {
            return new int[][]{
                    {Item.CARROT, 0, new Random().nextInt(3) + 1}
            };
        }
        return new int[][]{
                {Item.CARROT, 0, 1}
        };
    }

}
