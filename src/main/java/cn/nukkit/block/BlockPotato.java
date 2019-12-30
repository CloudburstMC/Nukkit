package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.utils.Identifier;

import java.util.Random;

/**
 * Created by Pub4Game on 15.01.2016.
 */
public class BlockPotato extends BlockCrops {

    public BlockPotato(Identifier id) {
        super(id);
    }

    @Override
    public Item toItem() {
        return Item.get(ItemIds.POTATO);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (getDamage() >= 0x07) {
            return new Item[]{
                    Item.get(ItemIds.POTATO, 0, new Random().nextInt(3) + 1)
            };
        } else {
            return new Item[]{
                    Item.get(ItemIds.POTATO)
            };
        }
    }
}
