package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.BlockIds.RED_MUSHROOM;

/**
 * Created by Pub4Game on 28.01.2016.
 */
public class BlockHugeMushroomRed extends BlockSolid {
    public static final int NONE = 0;
    public static final int TOP_NW = 1;
    public static final int TOP_N = 2;
    public static final int TOP_NE = 3;
    public static final int TOP_W = 4;
    public static final int TOP = 5;
    public static final int TOP_E = 6;
    public static final int TOP_SW = 7;
    public static final int TOP_S = 8;
    public static final int TOP_SE = 9;
    public static final int STEM = 10;
    public static final int ALL = 14;
    public static final int STEM_ALL = 15;

    public BlockHugeMushroomRed(Identifier id) {
        super(id);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public float getHardness() {
        return 0.2f;
    }

    @Override
    public float getResistance() {
        return 1;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (ThreadLocalRandom.current().nextInt(0, 20) == 0) {
            return new Item[]{
                    Item.get(RED_MUSHROOM)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }
}
