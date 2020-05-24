package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

public class BlockChorusPlant extends BlockTransparent {

    public BlockChorusPlant() {
    }

    @Override
    public int getId() {
        return CHORUS_PLANT;
    }

    @Override
    public String getName() {
        return "Chorus Plant";
    }

    @Override
    public double getHardness() {
        return 0.4;
    }

    @Override
    public double getResistance() {
        return 2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_NONE;
    }

    @Override
    public Item[] getDrops(Item item) {
        return ThreadLocalRandom.current().nextBoolean() ? new Item[]{Item.get(ItemID.CHORUS_FRUIT, 0, 1)} : new Item[0];
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PURPLE_BLOCK_COLOR;
    }
}
