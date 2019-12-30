package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

import java.util.concurrent.ThreadLocalRandom;


public class BlockSeaLantern extends BlockTransparent {
    public BlockSeaLantern(Identifier id) {
        super(id);
    }

    @Override
    public double getResistance() {
        return 1.5;
    }

    @Override
    public double getHardness() {
        return 0.3;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                Item.get(ItemIds.PRISMARINE_CRYSTALS, 0, ThreadLocalRandom.current().nextInt(2, 4))
        };
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }
}
